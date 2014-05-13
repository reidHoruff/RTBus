from django.core.management.base import BaseCommand, CommandError
from gcm.models import get_device_model
from routeservice.models import *

class Command(BaseCommand):
  args = '<poll_id poll_id ...>'
  help = 'Closes the specified poll for voting'

  def handle(self, *args, **options):
    self.stdout.write('notifying users...')
    devs = get_device_model().objects.all()
    for dev in devs:
      self.stdout.write((str(dev.reg_id)))
      dev.send_message("This is a test")

