from django.conf.urls import patterns, url

from track import views

urlpatterns = patterns('',
                       url(r'^$', views.index, name='index'),
)
