package picking;

import java.util.List;

public interface RoutingPickingListener
{
	void PickChanged(RoutingPointsIcon annotationChanged, List<RoutingPointsIcon> routingPointsAnnotations);
}
