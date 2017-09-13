package fi.nls.oskari.service.capabilities;

import fi.nls.oskari.mybatis.MyBatisHelper;
import fi.nls.oskari.annotation.Oskari;
import fi.nls.oskari.db.DatasourceHelper;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.ExecutorType;

import java.util.List;
import java.sql.Timestamp;

import javax.sql.DataSource;

@Oskari
public class CapabilitiesCacheServiceMybatisImpl extends CapabilitiesCacheService {

    private static final Logger LOG = LogFactory.getLogger(CapabilitiesCacheServiceMybatisImpl.class);

    private final SqlSessionFactory factory;

    public CapabilitiesCacheServiceMybatisImpl() {
        this(DatasourceHelper.getInstance().getDataSource());
    }

    public CapabilitiesCacheServiceMybatisImpl(DataSource ds) {
        this.factory = MyBatisHelper.initMyBatis(ds, CapabilitiesMapper.class);
    }

    private CapabilitiesMapper getMapper(SqlSession session) {
        return session.getMapper(CapabilitiesMapper.class);
    }

    /**
     * Tries to load capabilities from the database
     * @return null if not saved to db
     * @throws IllegalArgumentException if url or layertype is null or empty
     */
    public OskariLayerCapabilities find(String url, String layertype, String version)
            throws IllegalArgumentException {
        try (final SqlSession session = factory.openSession()) {
            return getMapper(session).findByUrlTypeVersion(url, layertype, version);
        }
    }

    /**
     * Inserts or updates a capabilities XML in database
     * The rows in the table are UNIQUE (url, layertype, version)
     */
    public OskariLayerCapabilities save(final OskariLayerCapabilities draft) {
        try (final SqlSession session = factory.openSession(false)) {
            final CapabilitiesMapper mapper = getMapper(session);

            String url = draft.getUrl();
            String type = draft.getLayertype();
            String version = draft.getVersion();

            // Check if a row already exists
            Long id = mapper.selectIdByUrlTypeVersion(url, type, version);
            if (id != null) {
                // Update
                mapper.updateData(id, draft.getData());
                OskariLayerCapabilities updated = mapper.findById(id);
                LOG.info("Updated capabilities:", updated);
                session.commit();
                return updated;
            } else {
                // Insert
                mapper.insert(draft);
                OskariLayerCapabilities inserted = mapper.findByUrlTypeVersion(url, type, version);
                LOG.info("Inserted capabilities:", inserted);
                session.commit();
                return inserted;
            }
        }
    }

    @Override
    protected List<OskariLayerCapabilities> getAllOlderThan(long maxAgeMs) {
        try (final SqlSession session = factory.openSession()) {
            final CapabilitiesMapper mapper = getMapper(session);
            final long time = System.currentTimeMillis() - maxAgeMs;
            final Timestamp ts = new Timestamp(time);
            LOG.debug("Getting all rows not updated since:", ts);
            List<OskariLayerCapabilities> list = mapper.findAllNotUpdatedSince(ts);
            LOG.debug("Found", list.size(), "row(s) not updated since:", ts);
            return list;
        }
    }

    @Override
    protected void updateMultiple(List<OskariLayerCapabilities> updates) {
        if (updates == null || updates.isEmpty()) {
            return;
        }
        try (final SqlSession session = factory.openSession(ExecutorType.BATCH, false)) {
            final CapabilitiesMapper mapper = getMapper(session);
            for (OskariLayerCapabilities capabilities : updates) {
                Long id = capabilities.getId();
                if (id == null) {
                    LOG.warn("Tried to update OskariLayerCapabilities with null id field! "
                            + "Capabilities:", capabilities);
                } else {
                    mapper.updateData(id, capabilities.getData());
                    LOG.info("Updated capabilities id:", id);
                }
            }
            session.commit();
        }
    }

}
