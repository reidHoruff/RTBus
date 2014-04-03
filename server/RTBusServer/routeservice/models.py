from django.db import models


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

  def dump_info(self):
    return {
        'name': self.name,
        'lat': str(self.lat),
        'lng': str(self.lng),
      }

  def __unicode__(self):
    return self.name


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

  def __unicode__(self):
    return str(self.lat) + ',' + str(self.lng)

