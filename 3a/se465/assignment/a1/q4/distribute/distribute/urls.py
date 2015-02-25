from django.conf.urls import patterns, include, url
from django.contrib import admin

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'distribute.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
    url(r'^track/', include('track.urls', namespace="track")),
    url(r'^admin/', include(admin.site.urls)),
)
