from django.db import models

class HoldingType(models.Model):
    name = models.CharField(max_length=20)
    # equity, bond, ...
    EQ = 'EQ'
    BD = 'BD'
    CS = '$$'
    ASSET_CLASS_CHOICES = ((EQ, 'equity'), (BD, 'bond'), (CS, 'cash'))
    asset_class = models.CharField(max_length=2,
                                   choices=ASSET_CLASS_CHOICES)
    # CA, US, INTL
    CA = 'CA'
    US = 'US'
    INTL = 'IN'
    COUNTRY_CHOICES = ((CA, 'CA'), (US, 'US'), (INTL, 'IN'))
    country = models.CharField(max_length=2,
                               choices=COUNTRY_CHOICES)
    minimum_holding_period = models.IntegerField()
    def __unicode__(self):
        return "[{0.name}] {0.asset_class}/{0.country}".format(self)

class HoldingTypeProportionRule(models.Model):
    percent = models.DecimalField(max_digits=4,decimal_places=2)
    holding_type = models.ForeignKey('HoldingType')
    def __unicode__(self):
        return "{0} @ {1}%".format(self.holding_type, self.percent)

class AccountType:
    RRSP = 'RRSP'
    TFSA = 'TFSA'
    NR = 'NR'
    ACCOUNT_TYPE_CHOICES = ((RRSP, 'RRSP'), (TFSA, 'TFSA'), (NR, 'NR'))

class Account(models.Model):
    name = models.CharField(max_length=20)
    account_type = models.CharField(max_length=4,
                                    choices=AccountType.ACCOUNT_TYPE_CHOICES)
    can_add_money = models.BooleanField(default=True)
    def __unicode__(self):
        return "{0} ({1})".format(self.name, self.account_type)

class Holding(models.Model):
    amount = models.DecimalField(max_digits=11,decimal_places=2)
    account = models.ForeignKey('Account')
    holding_type = models.ForeignKey('HoldingType')
    purchase_date = models.DateField()
    def __unicode__(self):
        return "${0} of {1}".format(self.amount, self.holding_type.name)

class HoldingLocationRule(models.Model):
    priority = models.IntegerField()
    holding_type = models.ForeignKey('HoldingType')
    account_type = models.CharField(max_length=4,
                                    choices=AccountType.ACCOUNT_TYPE_CHOICES)
    def __unicode__(self):
        return "{0} ({1} -> {2})".format(self.priority, self.holding_type, self.account_type)
