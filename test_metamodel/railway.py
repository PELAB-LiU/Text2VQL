from text2vql.metamodel import MetaModel

RAILWAY_METAMODEL = MetaModel('test_metamodel/railway.ecore')

NL1 = "Retrieve all segments whose lengths are less or equal than zero"
QUERY1 = """pattern posLength(segment : Segment){
    Segment.length(segment, length);
    check(length <= 0);
}"""

NL2 = "All switches that are not monitored by any sensor"
QUERY2 = """pattern switchMonitored(sw : Switch){
    neg find hasSensor(sw);
}

//auxiliary pattern
pattern hasSensor(sw : Switch){
    TrackElement.monitoredBy(sw, _);
}"""

# requires that all sensors associated with a switch that belongs to a route must also
# be associated directly with the same route
NL3 = "Retrieve sensors not required by a route but that route follows a switch position tracked by the given sensor"
QUERY3 = """pattern routeSensor(route : Route, sensor : Sensor, swP : SwitchPosition, sw : Switch){
    Route.follows(route, swP);
    SwitchPosition.target(swP, sw);
    TrackElement.monitoredBy(sw, sensor);
    neg find required(sensor, route);
}

//auxiliary pattern
pattern required(sensor : Sensor, route : Route){
    Route.requires(route, sensor);
}"""

# requires that an entry semaphore of
# an active route may show GO only if all switches along
# the route are in the position prescribed by the route
NL4 = "Give me all active routes whose semaphores say GO and its switch position is not the same as the switch current position"
QUERY4 = """
pattern switchSet(semaphore : Semaphore, route : Route, swP : SwitchPosition, sw : Switch){
	Route.active(route, true);
	Route.entry(route, semaphore);
	Route.follows(route, swP);
	SwitchPosition.target(swP, sw);
	Semaphore.signal(semaphore, Signal::GO);
	SwitchPosition.position(swP, swpPosition);
	Switch.currentPosition(sw, swCurrentPosition);
	swpPosition != swCurrentPosition;
}"""

NL5 = "Retrieve six segments monitored by a sensor and connected in a row (i.e., the first with the second, the second with the third, and so on)"
QUERY5 = """pattern connectedSegments(sensor : Sensor, segment1 : Segment, segment2 : Segment, segment3 : Segment, segment4 : Segment, segment5 : Segment, segment6 : Segment){
	Segment.connectsTo(segment1, segment2);
	Segment.connectsTo(segment2, segment3);
	Segment.connectsTo(segment3, segment4);
	Segment.connectsTo(segment4, segment5);
	Segment.connectsTo(segment5, segment6);
	Segment.monitoredBy(segment1, sensor);
	Segment.monitoredBy(segment2, sensor);
	Segment.monitoredBy(segment3, sensor);
	Segment.monitoredBy(segment4, sensor);
	Segment.monitoredBy(segment5, sensor);
	Segment.monitoredBy(segment6, sensor);
}"""

# requires routes which
# are connected through a pair of sensors and a pair of
# track elements to belong to the same semaphore.
NL6 = "Return two distinct routes that require two sensors that monitor two elements connected between them. The first rounte has a semaphore in its exist but the second does not have it in the entry."
QUERY6 = """
pattern semaphoreNeighbor(route1 : Route, route2 : Route, sensor1 : Sensor, sensor2 : Sensor, te1 : TrackElement, te2 : TrackElement) {
	Route.exit(route1, semaphore);
	Route.requires(route1, sensor1);
	TrackElement.monitoredBy(te1, sensor1);
	TrackElement.connectsTo(te1, te2);
	TrackElement.monitoredBy(te2, sensor2);
	Route.requires(route2, sensor2);
	neg find entrySemaphore(route2, semaphore);
	route1 != route2;
}

//auxiliary pattern
pattern entrySemaphore(route, semaphore){
	Route.entry(route, semaphore);
}"""






