package se.liu.ida.sas.pelab.text2vql.ocl;

public class RailwayOCLQueries {
    public static final String SIX_SEGMENT = """
            package railway
              context Sensor
              def foo : connectedSegments : Bag(Tuple(s1:TrackElement)) = Sensor.allInstances()-> collect(sensor|
                sensor.monitors -> select(oclIsTypeOf(Segment)) -> collect(segment1 |
                  segment1.connectsTo->select(oclIsTypeOf(Segment))->select(segment2 |
                    segment2.monitoredBy->includes(sensor))->collect(segment2 |
                    segment2.connectsTo->select(oclIsTypeOf(Segment))->select(segment3 |
                      segment3.monitoredBy->includes(sensor))->collect(segment3 |
                      segment3.connectsTo->select(oclIsTypeOf(Segment))->select(segment4 |
                        segment4.monitoredBy->includes(sensor))->collect(segment4 |
                        segment4.connectsTo->select(oclIsTypeOf(Segment))->select(segment5 |
                          segment5.monitoredBy->includes(sensor))->collect(segment5 |
                          segment5.connectsTo->select(oclIsTypeOf(Segment))->select(segment6 |
                            segment6.monitoredBy->includes(sensor))->collect(segment6 | Tuple{s1=segment6})))))))
            endpackage
    """;
    public static final String SIX_SEGMENT2 = """
            package railway
              context Sensor
              def foo : connectedSegments : Bag(Tuple(s1:Segment,s2:Segment,s3:Segment,s4:Segment,s5:Segment,s6:Segment)) = Sensor.allInstances()-> collect(sensor|
                sensor.monitors -> select(oclIsTypeOf(Segment)) -> collect(segment1 |
                  segment1.connectsTo->select(oclIsTypeOf(Segment))->select(segment2 |
                    segment2.monitoredBy->includes(sensor))->collect(segment2 |
                    segment2.connectsTo->select(oclIsTypeOf(Segment))->select(segment3 |
                      segment3.monitoredBy->includes(sensor))->collect(segment3 |
                      segment3.connectsTo->select(oclIsTypeOf(Segment))->select(segment4 |
                        segment4.monitoredBy->includes(sensor))->collect(segment4 |
                        segment4.connectsTo->select(oclIsTypeOf(Segment))->select(segment5 |
                          segment5.monitoredBy->includes(sensor))->collect(segment5 |
                          segment5.connectsTo->select(oclIsTypeOf(Segment))->select(segment6 |
                            segment6.monitoredBy->includes(sensor))->collect(segment6 |
                              Tuple{
                                s1 = segment1,
                                s2 = segment2,
                                s3 = segment3,
                                s4 = segment4,
                                s5 = segment5,
                                s6 = segment6
                              }
                            )))))))
            endpackage
    """;
}
