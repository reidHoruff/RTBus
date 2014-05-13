from django.core.management.base import BaseCommand, CommandError
from gcm.models import get_device_model
from routeservice.models import *
from routeservice.views import RealTimeRoutes
import time
import math


class Command(BaseCommand):
  m = 1.0
  args = '<poll_id poll_id ...>'
  help = 'Closes the specified poll for voting'

  def handle(self, *args, **options):
    while True:
      #self.stdout.write('building device map...')

      devmap = dict()
      devs = get_device_model().objects.all()
      for dev in devs:
        devmap[dev.dev_id] = dev

      routes = BusWroute.objects.all()
      route_pos_map = dict()
      for r in routes:
        loc = RealTimeRoutes.get_pos(r.id, False)
        if loc:
          route_pos_map[r.id] = float(loc['lat']), float(loc['lng'])
        else:
          route_pos_map[r.id] = None


      subs = StopSubscription.objects.all()
      for sub in subs:
        loc = route_pos_map[sub.stop.route.id]
        #self.stdout.write(str(loc))
        if loc:
          sub_pos = float(sub.stop.lat), float(sub.stop.lng)
          lat_dif = sub_pos[0] - loc[0]
          lng_dif = sub_pos[1] - loc[1]
          dist = math.sqrt(lat_dif**2 + lng_dif**2)
          self.stdout.write(str(dist))
          if dist < 0.0005:
            devmap[sub.device].send_message("foobar fuck you")
            self.stdout.write("sending message...")

      time.sleep(3)

