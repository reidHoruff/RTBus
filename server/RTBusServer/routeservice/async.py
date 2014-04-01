from sniper.snipers import *
import sniper.decorators as sniper

@sniper.ajax()
def get_live(request):
  yield InsertTemplate("#foo", "insert.html")
