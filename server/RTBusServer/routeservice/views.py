from easy.decorators import *
from routeservice.models import *
import time

class RealTimeRoutes:
  routes = dict()

  @staticmethod
  def set_pos(id, lat, lng):
    RealTimeRoutes.routes[str(id)] = {
        'lat': lat,
        'lng': lng,
        'time': int(time.time())
      }

  @staticmethod
  def get_pos(id):
    info = RealTimeRoutes.routes[str(id)]
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
  except KeyError:
    return {
        'success': False,
        'message': 'Invalid Parameters.',
      }

  pos = RealTimeRoutes.get_pos(id=id)
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


