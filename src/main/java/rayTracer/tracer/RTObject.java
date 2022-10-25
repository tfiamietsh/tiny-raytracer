package rayTracer.tracer;

import java.util.List;

public interface RTObject {
    RTObject newInstance(List<Double> dblParams, List<String> strParams);

    String getPattern(int paramCount);
}
