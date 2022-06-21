package org.czb.xingcan.admin.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.czb.xingcan.db.domain.SearchHistory;
import org.czb.xingcan.db.service.SearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchHistoryController {

    @Autowired
    private SearchHistoryService searchHistoryService;

    @GetMapping("/hotHistory")
    public List<SearchHistory> getHotHistory() {
        QueryWrapper<SearchHistory> wr = new QueryWrapper<>();
        wr.eq("source","wx");
        wr.eq("delete_flag",0);
        wr.last("limit 10");
        wr.orderByDesc("frequency");
        return searchHistoryService.list(wr);
    }

    @GetMapping("/history")
    public List<SearchHistory> getHistory() {
        QueryWrapper<SearchHistory> wr = new QueryWrapper<>();
        wr.eq("source","wx");
        wr.eq("delete_flag",0);
        wr.eq("user_id",1);
        wr.last("limit 10");
        wr.orderByDesc("update_time");
        return searchHistoryService.list(wr);
    }

    @PostMapping("/confirmHistory")
    public String getConfirmHistory(int id,String keyword) {
        QueryWrapper<SearchHistory> wr = new QueryWrapper<>();
        wr.eq("source","wx");
        wr.eq("user_id",id);
        wr.eq("keyword",keyword);
        SearchHistory searchHistory =searchHistoryService.getOne(wr);
        if(searchHistory!=null){
            UpdateWrapper<SearchHistory> uwr = new UpdateWrapper<>();
            uwr.set("update_time",new Date());
            uwr.set("delete_flag",0);
            uwr.set("frequency",searchHistory.getFrequency()+1);
            uwr.eq("id",searchHistory.getId());
            searchHistoryService.update(uwr);
        }else {
            searchHistory = new SearchHistory();
            searchHistory.setUserId(id);
            searchHistory.setFrequency(1);
            searchHistory.setKeyword(keyword);
            searchHistory.setUpdateTime(new Date());
            searchHistory.setAddTime(new Date());
            searchHistoryService.save(searchHistory);
        }
        return "success";
    }

    @PostMapping("/clearHistory")
    public String clearHistory(int id) {
        UpdateWrapper<SearchHistory> uwr = new UpdateWrapper<>();
        uwr.set("update_time",new Date());
        uwr.set("delete_flag",1);
        uwr.eq("user_id",id);
        searchHistoryService.update(uwr);
        return "success";
    }
}
