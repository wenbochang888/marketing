package com.wenbo.marketing.service;

import com.wenbo.marketing.dao.MktActivityInfoDAO;
import com.wenbo.marketing.model.MktActivityInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author changwenbo
 * @date 2024/8/23 16:16
 */
@Slf4j
@Service
public class MktActivityService {

    private static final String EMPTY = StringUtils.EMPTY;

    @Autowired
    private MktActivityInfoDAO mktActivityInfoDAO;

    public String getNewestActivity() {
        List<MktActivityInfo> activityInfoList = mktActivityInfoDAO.selectList(null);
        if (CollectionUtils.isEmpty(activityInfoList)) {
            return EMPTY;
        }

        return activityInfoList.stream().max((x, y) -> -x.getCreatedAt().compareTo(y.getCreatedAt())).get().getActivityId();
    }
}
