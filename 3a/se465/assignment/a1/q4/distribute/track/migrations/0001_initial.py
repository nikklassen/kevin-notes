# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Account',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(max_length=20)),
                ('account_type', models.CharField(max_length=4, choices=[(b'RRSP', b'RRSP'), (b'TFSA', b'TFSA'), (b'NR', b'NR')])),
                ('can_add_money', models.BooleanField(default=True)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Holding',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('amount', models.DecimalField(max_digits=11, decimal_places=2)),
                ('purchase_date', models.DateField()),
                ('account', models.ForeignKey(to='track.Account')),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='HoldingLocationRule',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('priority', models.IntegerField()),
                ('account_type', models.CharField(max_length=4, choices=[(b'RRSP', b'RRSP'), (b'TFSA', b'TFSA'), (b'NR', b'NR')])),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='HoldingType',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(max_length=20)),
                ('asset_class', models.CharField(max_length=2, choices=[(b'EQ', b'equity'), (b'BD', b'bond'), (b'$$', b'cash')])),
                ('country', models.CharField(max_length=2, choices=[(b'CA', b'CA'), (b'US', b'US'), (b'IN', b'IN')])),
                ('minimum_holding_period', models.IntegerField()),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='HoldingTypeProportionRule',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('percent', models.DecimalField(max_digits=4, decimal_places=2)),
                ('holding_type', models.ForeignKey(to='track.HoldingType')),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.AddField(
            model_name='holdinglocationrule',
            name='holding_type',
            field=models.ForeignKey(to='track.HoldingType'),
            preserve_default=True,
        ),
        migrations.AddField(
            model_name='holding',
            name='holding_type',
            field=models.ForeignKey(to='track.HoldingType'),
            preserve_default=True,
        ),
    ]
