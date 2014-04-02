##RTBus Server
This directory will contain a web server that backs the android RTBus app.

This server will store routes and and provide real time bus tracking information.


##Installation
N/A


##API Reference
###Create Route
/create\_route?name=NameOfRoute

```
{
    "load": {
      "id": 4
    }, 
    "message": "Route Created", 
    "success": true
}
```

###Get Route
get\_route/?id=RouteID

```
{
  "load": {
    "name": "Route A", 
      "coordinates": [
      {
        "lat": "23.23424444440000000000", 
        "lng": "11.11111110000000000000"
      }, 
      {
        "lat": "23.23424444440000000000", 
        "lng": "11.11111110000000000000"
      }, 
      {
        "lat": "23.23424444440000000000", 
        "lng": "11.11111110000000000000"
      }, 
      {
        "lat": "23.23424444440000000000", 
        "lng": "11.11111110000000000000"
      }, 
      {
        "lat": "23.23424444440000000000", 
        "lng": "11.11111110000000000000"
      }
    ], 
      "stops": [
      {
        "lat": "12.12334234230000000000", 
        "lng": "12.33333330000000000000", 
        "name": "Stop A"
      }
    ]
  }, 
    "success": true
}
```

###Get Route List
/get\_route\_list/

```
[
  {
    "name": "Route A", 
      "id": 1
  }, 
  {
    "name": "foobarfoo", 
    "id": 2
  }, 
  {
    "name": "FooBar", 
    "id": 3
  }, 
  {
    "name": "FooBar", 
    "id": 4
  }
]
```

###Add Coordinate
/add\_coordinate/?id=ID&lat=LAT&lng=LNG
```
{
  "message": "Coordinate Added", 
  "success": true
}
```

###Set Current Bus Position
/set\_cur\_pos/?id=RouteID&lat=LAT&lng=LNG


```
{
  "message": "Position Updated.", 
  "success": true
}
```


###Get Current Bus Position
/get\_cur\_pos/?id=RouteID
```
{
  "load": {
    "lat": "12.345", 
      "diff": 98, 
      "lng": "123.45", 
      "time": 1396475803
  }, 
    "success": true
}
```
