from django.db import models
from gcm.models import get_device_model
import random


class BusWroute(models.Model):
  name = models.CharField(max_length=200)
  #coordinates
  #stops

  @staticmethod
  def create_wroute(name):
    route = BusWroute.objects.create(name=name)
    return route.id

  def add_coordinate(self, lat, lng):
    Coordinate.create_coordinate(lat, lng, self)

  def add_stop(self, lat, lng, name):
    BusStop.create_stop(lat, lng, name, self)

  @staticmethod
  def get_route(id):
    return BusWroute.objects.get(id=id)

  @staticmethod
  def get_route_list_info():
    route_list = BusWroute.objects.all()
    return [{'name': x.name, 'id': x.id} for x in route_list]


  def dump_info(self):
    return {
        'name': self.name,
        'id': self.id,
        'coordinates': [x.dump_info() for x in self.coordinates.all()],
        'stops': [x.dump_info() for x in self.stops.all()],
      }

  def __unicode__(self):
    return self.name + ", " + str(self.id)


class BusStop(models.Model):
  name = models.CharField(max_length=300)
  route = models.ForeignKey('BusWroute', related_name='stops')
  lat = models.DecimalField(max_digits=25, decimal_places=20)
  lng = models.DecimalField(max_digits=25, decimal_places=20)


  @staticmethod
  def create_stop(lat, lng, name, route):
    BusStop.objects.create(
        name=name,
        lat=lat,
        lng=lng,
        route=route)

  def dump_info(self):
    return {
        'name': self.name,
        'lat': str(self.lat),
        'lng': str(self.lng),
        'route_name': str(self.route.name),
        'route_id': int(self.route.id),
        'id': self.id,
      }

  def __unicode__(self):
    return self.name + " " + str(self.id)


class Coordinate(models.Model):
  lat = models.DecimalField(max_digits=25, decimal_places=20)
  lng = models.DecimalField(max_digits=25, decimal_places=20)
  route = models.ForeignKey('BusWroute', related_name='coordinates')

  @staticmethod
  def create_coordinate(lat, lng, route):
    Coordinate.objects.create(lat=lat, lng=lng, route=route)


  def dump_info(self):
    return {
        'lat': str(self.lat),
        'lng': str(self.lng),
      }

  @staticmethod
  def get_random(route_id):
    results = list(Coordinate.objects.filter(route__id=route_id))
    random.shuffle(results)
    return results[0]

  def __unicode__(self):
    return str(self.lat) + ',' + str(self.lng)

class RealTimeCoordinates(models.Model):
  lat = models.DecimalField(max_digits=25, decimal_places=20)
  lng = models.DecimalField(max_digits=25, decimal_places=20)
  route = models.IntegerField()
  time = models.IntegerField()


  @staticmethod
  def create(lat, lng, route, time):
    RealTimeCoordinates.objects.create(lat=lat, lng=lng, route=route, time=time)

  @staticmethod
  def get(id):
    try:
      return RealTimeCoordinates.objects.get(route=id)
    except:
      return None

  @staticmethod
  def set(route, lat, lng, time):
    foo = RealTimeCoordinates.get(route)

    if foo:
      foo.lat = lat
      foo.lng = lng
      foo.time = time
      foo.save()
    else:
      RealTimeCoordinates.create(lat, lng, route, time)

  def dump_info(self):
    return {
        'lat': str(self.lat),
        'lng': str(self.lng),
        'time': self.time,
      }

  def __unicode__(self):
    return str(self.lat) + ',' + str(self.lng)



class StopSubscription(models.Model):
  device = models.CharField(max_length=200)
  stop = models.ForeignKey('BusStop', related_name='subscribers')
  h = models.IntegerField()
  m = models.IntegerField()

  def dump_info(self):
    return {
        'device': self.device,
        'h': self.h,
        'm': self.m,
        'id': self.id,
        'stop_name': self.stop.name,
        'stop_id': self.stop.id,
      }

  @staticmethod
  def get_subs(device):
    subs = StopSubscription.objects.filter(device=device)
    return [s.dump_info() for s in subs]
