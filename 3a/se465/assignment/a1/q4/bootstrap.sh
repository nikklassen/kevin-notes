#!/usr/bin/env bash

apt-get update
apt-get install -y python python-pip libpython-dev git
pip install django pillow dnspython pytz coverage django-nose
git clone https://github.com/patricklam/isin.git
chown -R vagrant.vagrant isin
cd ~vagrant/isin
sudo -u vagrant python manage.py migrate
cd ~vagrant/distribute
sudo -u vagrant python manage.py migrate
sudo -u vagrant python manage.py loaddata starting_data
sudo -u vagrant sh -c 'cat create-root-user | python manage.py shell'

