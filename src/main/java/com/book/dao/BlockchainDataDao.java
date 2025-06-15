package com.book.dao;

import com.book.domain.BlockchainDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BlockchainDataDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BlockchainDataDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 根据条件查询数据
    public List<BlockchainDataEntity> findByCriteria(
            String cryptocurrency, 
            String metric, 
            String analysisType
    ) {
        String sql = "SELECT * FROM blockchain_data " +
                     "WHERE cryptocurrency = ? " +
                     "AND metric = ? " +
                     "AND analysis_type = ? " +
                     "ORDER BY x_value";

        return jdbcTemplate.query(sql, new Object[]{cryptocurrency, metric, analysisType}, 
            new RowMapper<BlockchainDataEntity>() {
                @Override
                public BlockchainDataEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                    BlockchainDataEntity entity = new BlockchainDataEntity();
                    entity.setId(rs.getLong("id"));
                    entity.setCryptocurrency(rs.getString("cryptocurrency"));
                    entity.setMetric(rs.getString("metric"));
                    entity.setAnalysisType(rs.getString("analysis_type"));
                    entity.setxValue(rs.getDouble("x_value"));
                    entity.setyValue(rs.getDouble("y_value"));
                    entity.setAdditionalInfo(rs.getString("additional_info"));
                    return entity;
                }
            }
        );
    }
    
    // 插入数据
    public void insertData(BlockchainDataEntity data) {
        String sql = "INSERT INTO blockchain_data " +
                     "(cryptocurrency, metric, analysis_type, x_value, y_value, additional_info) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql, 
            data.getCryptocurrency(),
            data.getMetric(),
            data.getAnalysisType(),
            data.getxValue(),
            data.getyValue(),
            data.getAdditionalInfo()
        );
    }
}