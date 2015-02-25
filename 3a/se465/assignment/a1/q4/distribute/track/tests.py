from django.test import TestCase

from .models import (Account, Holding, HoldingType, HoldingTypeProportionRule)
from .views import (calculate_maxima, calculate_current_holdings, calculate_raw_allocations, apply_allocation_rules, apply_allocations_to_accounts)

class ModelsTestCase(TestCase):
    def setUp(self):
        for name, asset in (('equity', 'EQ'), ('bond', 'BD'), ('cash', '$$')):
            for country in ('CA', 'US', 'IT'):
                for min_per in xrange(10):
                    HoldingType.objects.create(name=name, asset_class=asset, country=country, minimum_holding_period=min_per)

        HoldingTypeProportionRule.objects.create(percent=42.01, holding_type_id=1)

    def testHoldingType(self):
        self.assertEqual(HoldingType.objects.get(name='equity', country='CA', minimum_holding_period=1).__unicode__(), '[equity] EQ/CA')
        self.assertEqual(HoldingType.objects.get(asset_class='$$', country='IT', minimum_holding_period=8).__unicode__(), '[cash] $$/IT')
        self.assertEqual(HoldingType.objects.get(asset_class='$$', country='IT', minimum_holding_period=3).__unicode__(), '[cash] $$/IT')

    def testHoldingTypeProportionRule(self):
        self.assertEqual(HoldingType.objects.get(id=1), HoldingTypeProportionRule.objects.get(percent=42.01).holding_type)
        self.assertEqual(HoldingTypeProportionRule.objects.get(id=1).holding_type.__unicode__() + ' @ 42.01%', HoldingTypeProportionRule.objects.get(id=1).__unicode__())

    def testRemainingModels(self):
        # since Model tests are mostly tests of Django behaviour, they have been omitted for brevity
        pass

class ViewsTestCase(TestCase):
    def setUp(self):
        Account.objects.create(name='acc1', account_type='RRSP', can_add_money=False)
        Account.objects.create(name='acc2', account_type='TFSA', can_add_money=True)
        Account.objects.create(name='acc3', account_type='NR', can_add_money=True)

        HoldingType.objects.create(name='hold1', asset_class='EQ', country='CA', minimum_holding_period=1)
        HoldingType.objects.create(name='hold2', asset_class='$$', country='CA', minimum_holding_period=1)

        HoldingTypeProportionRule.objects.create(percent=60, holding_type_id=1)
        HoldingTypeProportionRule.objects.create(percent=40, holding_type_id=2)

        Holding.objects.create(amount=123, account_id=1, holding_type_id=1, purchase_date='2015-02-03')
        Holding.objects.create(amount=234, account_id=2, holding_type_id=1, purchase_date='2015-02-03')
        Holding.objects.create(amount=432, account_id=2, holding_type_id=2, purchase_date='2015-02-03')
        Holding.objects.create(amount=345, account_id=3, holding_type_id=1, purchase_date='2015-02-03')

    def testCalculateMaxima(self):
        self.assertEquals(
            calculate_maxima(),
            (
                {
                    Account.objects.get(name='acc1'): (123, 'N/A'),
                    Account.objects.get(name='acc2'): (666, 666),
                    Account.objects.get(name='acc3'): (345, 345),
                },
                {
                    Account.objects.get(name='acc1'): 'N/A',
                    Account.objects.get(name='acc2'): 666,
                    Account.objects.get(name='acc3'): 345,
                }
            )
        )

    def testCalculateCurrentHoldings(self):
        investments, holdings = calculate_current_holdings()

        self.assertEquals(
            investments,
            {
                'hold1': 702,
                'hold2': 432,
            }
        )
        self.assertEquals(
            map(lambda x: [y[0].__unicode__() for y in x] if isinstance(x, list) else x, holdings.values()),
            [
                [
                    Holding.objects.create(amount='123', holding_type_id=1, account_id=1, purchase_date='2015-02-03').__unicode__(),
                ],
                [
                    Holding.objects.create(amount='234', holding_type_id=1, account_id=2, purchase_date='2015-02-03').__unicode__(),
                    Holding.objects.create(amount='432', holding_type_id=2, account_id=2, purchase_date='2015-02-03').__unicode__(),
                ],
                [
                    Holding.objects.create(amount='345', holding_type_id=1, account_id=3, purchase_date='2015-02-03').__unicode__(),
                ],
            ]
        )

    def testCalculateRawAllocations(self):
        self.assertEquals(
            calculate_raw_allocations(100, {}),
            (
                [
                    ['hold1', 0, 60],
                    ['hold2', 0, 40],
                ],
                {
                    HoldingType.objects.get(name='hold1'): 60,
                    HoldingType.objects.get(name='hold2'): 40,
                },
            )
        )

        self.assertEquals(
            calculate_raw_allocations(50, {'hold1': 1000}),
            (
                [
                    ['hold1', 1000, 30],
                    ['hold2', 0, 20],
                ],
                {
                    HoldingType.objects.get(name='hold1'): 30,
                    HoldingType.objects.get(name='hold2'): 20,
                },
            )
        )

    def testApplyAllocationRules(self):
        self.assertEquals(
            apply_allocation_rules(
                {'acc1': '123', 'acc2': 'N/A', 'acc3': 'N/A'},
                {'hold1': 50, 'hold2': 75},
            ),
            {}
        )

    def testApplyAllocationsToAccounts(self):
        original = {
            'TFSA': [
                (Holding.objects.create(amount=543, account_id=1, holding_type_id=1, purchase_date='2015-02-03'), 0),
                (Holding.objects.create(amount=687, account_id=3, holding_type_id=2, purchase_date='2015-02-03'), 0),
            ],
            'RRSP': [
                (Holding.objects.create(amount=465, account_id=2, holding_type_id=1, purchase_date='2015-02-03'), 0),
            ],
        }
        expected = {
            'RRSP': [
                (Holding.objects.create(amount=465, account_id=1, holding_type_id=1, purchase_date='2015-02-03'), 0),
                (Holding.objects.create(amount=0, account_id=1, holding_type_id=2, purchase_date='2015-02-03'), 1)
            ],
            'TFSA': [
                (Holding.objects.create(amount=543, account_id=1, holding_type_id=1, purchase_date='2015-02-03'), 0),
                (Holding.objects.create(amount=687, account_id=1,holding_type_id=2, purchase_date='2015-02-03'), 0)
            ]
        }

        apply_allocations_to_accounts(
            {
                'TFSA': [
                    (Holding.objects.create(amount=123, account_id=1, holding_type_id=1, purchase_date='2015-02-03'), 0)
                ],
                'RRSP': [
                    (Holding.objects.create(amount=321, account_id=2, holding_type_id=2, purchase_date='2015-02-03'), 0),
                    (Holding.objects.create(amount=321, account_id=2, holding_type_id=2, purchase_date='2015-02-03'), 1)
                ]
            },
            original
        )

        self.assertEquals(
            map(lambda x: x, original),
            map(lambda x: x, expected)
        )
