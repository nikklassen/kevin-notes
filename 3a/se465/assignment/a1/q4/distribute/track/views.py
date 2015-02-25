from django.shortcuts import render
from track.models import HoldingTypeProportionRule, HoldingType, Holding, Account, HoldingLocationRule
from decimal import *
from datetime import date

def calculate_maxima():
    """ Calculate the maximum amount that can go in each account. 

    Return values:
    max_amounts -- maps account to (current_contents, max_contents_OR_NA)
    room_left -- maps account to max_contents_OR_NA; later users update the amount left

    Invariant: map(lambda(x): x[2], max_amounts) == room_left
    """
    amounts = {} #: amount of money already in each account
    for a in Account.objects.all():
        amounts[a] = 0

    for h in Holding.objects.all():
        amounts[h.account] += h.amount

    max_amounts = {}
    room_left = {}
    for a in Account.objects.all():
        if not a.can_add_money:
            m = "N/A"
        else:
            m = amounts[a]
        max_amounts[a] = (amounts[a], m)
        room_left[a] = m
    return (max_amounts, room_left)

def calculate_current_holdings():
    """ Returns two dicts summarizing aspects of current holdings.

    Return values:
    current_investments -- for each of the holdings, dollar amount held
    current_holdings -- for each account, a list of tuples (holding name/amount, 0)

    Later users of current_holdings update the 0 to be the ideal amount of that holding.
    """
    current_investments = {} #: amount of money per holding type
    for ht in HoldingType.objects.all():
        for h in Holding.objects.filter(holding_type__name=ht.name):
            current_investments[ht.name] = current_investments.setdefault(ht.name, 0) + h.amount

    current_holdings = {}
    for a in Account.objects.all():
        for ht in Holding.objects.filter(account=a):
            if not (a in current_holdings):
                current_holdings[a] = []
            current_holdings[a].append((ht, 0))
    return current_investments, current_holdings

def calculate_raw_allocations(total, current_investments):
    """ Following the HoldingTypeProportionRules, returns how much money to put in each holding. 

    Precondition: ar.percents sum to 100. (So, you're not allowed to test behaviour when that precondition isn't met.)

    remaining_amount_to_allocate gets updated by apply_allocation_rules. 
    """
    raw_allocations = [] #: according to the rules, how much of each holding (1) currently held; (2) to hold
    remaining_amount_to_allocate = {} #: amount still to allocate, for each holding type
    for ar in HoldingTypeProportionRule.objects.all().order_by('holding_type__name'):
        amt = ar.percent * total / 100
        raw_allocations.append([ar.holding_type.name, current_investments.setdefault(ar.holding_type.name, 0), amt])
        remaining_amount_to_allocate[ar.holding_type] = remaining_amount_to_allocate.setdefault(ar.holding_type, 0) + amt
    return (raw_allocations, remaining_amount_to_allocate)

def apply_allocation_rules(room_left, remaining_amount_to_allocate):
    """ Throw money into accounts following the HoldingLocationRules while not violating the room_left constraints. """
    allocated_amounts = {}
    for lr in HoldingLocationRule.objects.all().order_by('priority'):
        for a in Account.objects.filter(account_type = lr.account_type):
            remaining_amount = remaining_amount_to_allocate[lr.holding_type]
            allocated_amounts.setdefault(a, [])
            h = Holding(account=a, holding_type=lr.holding_type, purchase_date=date.today())
            if (room_left[a] == 'N/A' or room_left[a] > remaining_amount):
                h.amount = remaining_amount
                allocated_amounts[a].append((h, remaining_amount))
                remaining_amount_to_allocate[lr.holding_type] = 0
                if room_left[a] != 'N/A':
                    room_left[a] -= remaining_amount
            else:
                h.amount = room_left[a]
                allocated_amounts[a].append((h, room_left[a]))
                remaining_amount_to_allocate[lr.holding_type] -= room_left[a]
                if room_left[a] != 'N/A':
                    room_left[a] = 0
    return allocated_amounts

def apply_allocations_to_accounts(allocated_amounts, current_holdings):
    """ Update the current_holdings dict w/info from allocated_amounts. """
    for a in allocated_amounts:
        for h in allocated_amounts[a]:
            found = False
            for idx, ch in enumerate(current_holdings[a]):
                if ch[0].holding_type == h[0].holding_type:
                    found = True
                    current_holdings[a][idx] = (ch[0], ch[1] + h[1])
            if not found and h[1] > 0:
                h[0].amount = 0
                current_holdings[a].append((h[0], h[1]))
        current_holdings[a].sort(key=lambda x: x[0].holding_type.name)

def index(request):
    new_amount = 0 #: amount of money to add
    total = 0 #: total pool of money available
    if 'new_amount' in request.POST:
        new_amount = Decimal(request.POST['new_amount'])
        total = new_amount
    for h in Holding.objects.all():
        total += h.amount

    max_amounts, room_left = calculate_maxima()
    current_investments, current_holdings = calculate_current_holdings()
    raw_allocations, remaining_amount_to_allocate = calculate_raw_allocations(total, current_investments)
    allocated_amounts = apply_allocation_rules(room_left, remaining_amount_to_allocate)
    apply_allocations_to_accounts(allocated_amounts, current_holdings)

    context = {'new_amount' : new_amount,
               'total' : total,
               'max_amounts' : max_amounts,
               'raw_allocations' : raw_allocations,
               'current_holdings' : current_holdings}
    return render(request, 'track/index.html', context)
