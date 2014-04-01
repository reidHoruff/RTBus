from easy.decorators import *


class RealTimeRoutes:
  routes = dict()

  @staticmethod
  def set_pos(id, lat, lng):
    RealTimeRoutes.routes[id] = (lat, lng)

  @staticmethod
  def get_pos(id):
    if id in RealTimeRoutes.routes:
      return RealTimeRoutes.routes[id]
    else:
      return None

@context_template_response
def home(request):
  return "home.html"


@json_response
def set_pos(request):
  RealTimeRoutes.set_pos(
      id=request.REQUEST['id'],
      lat=request.REQUEST['lat'],
      lng=request.REQUEST['lng'],
    )

  return {'success':True}

@json_response
def get_pos(request):
  pos = RealTimeRoutes.get_pos(id=request.REQUEST['id'])


  return {
      'success':True,
      'lat': pos[0],
      'lng': pos[1],
    }
