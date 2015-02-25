from django.contrib import admin
from track.models import HoldingType, Holding, HoldingTypeProportionRule, Account, HoldingLocationRule

@admin.register(HoldingLocationRule)
class HoldingLocationRuleAdmin(admin.ModelAdmin):
    ordering = ('priority', 'holding_type', 'account_type')

admin.site.register(HoldingType)
admin.site.register(Account)
admin.site.register(Holding)
admin.site.register(HoldingTypeProportionRule)
