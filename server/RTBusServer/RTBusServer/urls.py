from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    url(r'^$', 'routeservice.views.home', name='home'),
    url(r'^set_cur_pos/$', 'routeservice.views.set_cur_pos', name='set_pos'),
    url(r'^get_cur_pos/$', 'routeservice.views.get_cur_pos', name='get_pos'),

    url(r'^get_route/$', 'routeservice.views.get_route', name='get_route'),
    url(r'^create_route/$', 'routeservice.views.create_route', name='create_route'),
    url(r'^get_route_list/$', 'routeservice.views.get_route_list', name='get_route_list'),
    url(r'^add_coordinate/$', 'routeservice.views.add_coordinate', name='add_coordinate'),
    url(r'^add_stop/$', 'routeservice.views.add_stop', name='add_stop'),

    url(r'^admin/', include(admin.site.urls)),
)
