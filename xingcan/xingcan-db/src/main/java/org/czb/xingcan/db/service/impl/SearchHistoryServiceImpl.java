package org.czb.xingcan.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.czb.xingcan.db.dao.SearchHistoryMapper;
import org.czb.xingcan.db.domain.SearchHistory;
import org.czb.xingcan.db.service.SearchHistoryService;
import org.springframework.stereotype.Service;

@Service
public class SearchHistoryServiceImpl extends ServiceImpl<SearchHistoryMapper, SearchHistory> implements SearchHistoryService {

}
