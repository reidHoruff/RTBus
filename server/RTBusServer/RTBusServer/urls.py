from django.conf.urls import patterns, include, url

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    url(r'^$', 'routeservice.views.home', name='home'),
    url(r'^set_pos/$', 'routeservice.views.set_pos', name='set_pos'),
    url(r'^get_pos/$', 'routeservice.views.get_pos', name='get_pos'),
    url(r'^async/get_live/$', 'routeservice.async.get_live', name='async_get_live'),

    # url(r'^admin/', include(admin.site.urls)),
)
