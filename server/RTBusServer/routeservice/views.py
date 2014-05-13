from easy.decorators import *
from routeservice.models import *
from gcm.models import get_device_model
import time


class RealTimeRoutes:
  index = 0
  routes = dict()
  ind = dict()

  @staticmethod
  def set_pos(id, lat, lng):
    RealTimeCoordinates.set(id, lat, lng, int(time.time()))

  @staticmethod
  def get_pos(id, r=False):
    if r:
      coords = list(Coordinate.objects.filter(route__id=id))
      if not coords:
        return None
      if id not in RealTimeRoutes.ind:
        RealTimeRoutes.ind[id] = 0
      RealTimeRoutes.ind[id] += 3
      RealTimeRoutes.ind[id] %= len(coords)
      info = coords[RealTimeRoutes.ind[id]].dump_info()
      info['diff'] = 1
      info['index'] = RealTimeRoutes.ind[id]
      info['len'] = len(coords)
      RealTimeRoutes.set_pos(id=id, lat=float(info['lat']), lng=float(info['lng']))
      return info

    info = RealTimeCoordinates.get(id)

    if not info:
      return None

    info = info.dump_info()

    info['diff'] = int(time.time()) - info['time'] 
    return info

@context_template_response
def home(request):
  return "home.html"

@json_response
def set_cur_pos(request):
  try:
    id=request.REQUEST['id']
    lat=request.REQUEST['lat']
    lng=request.REQUEST['lng']
    print id
  except KeyError:
    return {
        'success': False,
        'message': 'Invalid Parameters.',
      }

  RealTimeRoutes.set_pos(id=id, lat=lat, lng=lng)
  return {'success': True, 'message': 'Position Updated.'}

@json_response
def get_cur_pos(request):
  try:
    id=request.REQUEST['id']
    r=request.REQUEST['r']
    if r == 'true':
      r = True
    else:
      r = False
  except KeyError:
    return {
        'success': False,
        'message': 'Invalid Parameters.',
      }

  pos = RealTimeRoutes.get_pos(id=id, r=r)
  print 'pos', pos

  if not pos:
    return {
        'success': False,
        'message': 'No data for this route.',
    }

  return {
      'success': True,
      'load': pos,
    }

@json_response
def get_route(request):
  try:
    route_id = request.REQUEST['id']
  except KeyError:
    return {
        'success': False,
        'message': 'Invalid Parameters.',
      }

  route = BusWroute.get_route(route_id)

  if route:
    return {
        'success': True,
        'load': route.dump_info()
      }

  else:
    return {'success': False, 'message': 'Route not found for id'}

@json_response
def create_route(request):
  try:
    name = request.REQUEST['name']
  except KeyError:
    return {
        'success': False,
        'message': 'Invalid Parameters.',
      }
  id = BusWroute.create_wroute(name=name)
  return {'success': True, 'message': 'Route Created', 
      'load': {'id': id}}

@json_response
def get_route_list(request):
  return {
      'success': True,
      'dump': {
        'routes': BusWroute.get_route_list_info()
        }
      }

@json_response
def add_coordinate(request):
  try:
    lat = request.REQUEST['lat']
    lng = request.REQUEST['lng']
    id = request.REQUEST['id']
  except KeyError:
    return {
        'success': False,
        'message': 'Invalid Parameters.',
      }

  route = BusWroute.get_route(id)
  
  if not route:
    return {
        'success': False, 
        'message': 'Could not find route from specified id',
    }


  route.add_coordinate(lat, lng)

  return {
      'success': True,
      'message': 'Coordinate Added',
    }


@json_response
def add_stop(request):
  try:
    lat = request.REQUEST['lat']
    lng = request.REQUEST['lng']
    name = request.REQUEST['name']
    id = request.REQUEST['id']
  except KeyError:
    return {
        'success': False,
        'message': 'Invalid Parameters.',
      }

  route = BusWroute.get_route(id)
  
  if not route:
    return {
        'success': False, 
        'message': 'Could not find route from specified id',
    }


  route.add_stop(lat, lng, name)

  return {
      'success': True,
      'message': 'Stop Added',
    }


@json_response
def add_stop_sub(request):
  try:
    stop_id = request.REQUEST['stop_id']
    device = request.REQUEST['device']
    h = request.REQUEST['h']
    m = request.REQUEST['m']
  except KeyError:
    return {
        'success': False,
        'message': 'Invalid Parameters.',
      }

  StopSubscription.objects.create(
      device=device,
      stop=BusStop.objects.get(id=stop_id),
      h=h,
      m=m
    )

  return {
      'success': True,
      'message': 'Stop Subscription Added',
    }

@json_response
def get_stop_subs(request):
  try:
    device = request.REQUEST['device']
  except KeyError:
    return {
        'success': False,
        'message': 'Invalid Parameters.',
      }

  return {
      'success': True,
      'dump': StopSubscription.get_subs(device)
    }

@json_response
def remove_stop_sub(request):
  try:
    id = request.REQUEST['id']
    device = request.REQUEST['device']
  except KeyError:
    return {
        'success': False,
        'message': 'Invalid Parameters.',
      }

  StopSubscription.objects.get(id=id, device=device).delete()

  return {
      'success': True,
      'message': 'Stop sub removed.',
      'dump': StopSubscription.get_subs(device)
    }

@json_response
def get_all_routes(request):
  all_routes = BusWroute.objects.all()

  return {
      'success': True,
      'dump': [r.dump_info() for r in all_routes]
    }

@json_response
def sub_gcm(request):
  try:
    reg_id = request.REQUEST['reg_id']
    dev_id = request.REQUEST['dev_id']
  except KeyError:
    return {
        'success': False,
        'message': 'Invalid Parameters.',
      }


  print "creating device sub"
  try:
    get_device_model().objects.create(
        name=dev_id,
        dev_id=dev_id,
        reg_id=reg_id
        )
  except: 
    return {
        'success': False,
        'message': 'Registration Failed',
      }

  return {
      'success': True,
      'message': 'Device registered',
      'reg_id': reg_id,
      'dev_id': dev_id,
    }

