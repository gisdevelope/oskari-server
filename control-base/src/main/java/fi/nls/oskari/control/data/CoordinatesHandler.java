package fi.nls.oskari.control.data;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.domain.geo.Point;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.geometry.PointTransformer;
import fi.nls.oskari.map.geometry.ProjectionHelper;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.ResponseHelper;
import org.json.JSONObject;

import static fi.nls.oskari.control.ActionConstants.*;

@OskariActionRoute("Coordinates")
public class CoordinatesHandler extends ActionHandler {

    private static final Logger LOG = LogFactory.getLogger(CoordinatesHandler.class);

    static final String TARGET_SRS = "targetSRS";

    private PointTransformer service = null;

    @Override
    public void handleAction(final ActionParameters params)
            throws ActionException {

        final Point point = new Point(params.getRequiredParamDouble(PARAM_LON),
                params.getRequiredParamDouble(PARAM_LAT));
        final String srs = params.getRequiredParam(PARAM_SRS);
        final String target = params.getRequiredParam(TARGET_SRS);

        LOG.debug("Params - lon", point.getLon(), "lat", point.getLat(), "in", srs);
        try {
            PointTransformer transformer = getTransformer();
            Point value = transformer.reproject(point, srs, target);
            LOG.debug("Reprojected - lon", value.getLon(), "lat", value.getLat(), "in", target);
            JSONObject response = new JSONObject();
            JSONHelper.putValue(response, PARAM_LON, value.getLon());
            JSONHelper.putValue(response, PARAM_LAT, value.getLat());
            ResponseHelper.writeResponse(params, response);

        } catch (RuntimeException ex) {
            throw new ActionParamsException(ex.getMessage());
        }
    }

    private PointTransformer getTransformer() {
        if(service == null) {
            service = new ProjectionHelper();
        }
        return service;
    }
}
