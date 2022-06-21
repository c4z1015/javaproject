package org.czb.xingcan.admin.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.czb.xingcan.db.domain.ImageChart;
import org.czb.xingcan.db.service.ImageChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageChartController {

    @Autowired
    private ImageChartService imageChartService;

    @GetMapping("/swiperList")
    public List<ImageChart> getSwiper() {
        QueryWrapper<ImageChart> wr = new QueryWrapper<>();
        wr.eq("func_type","swiper");
        return imageChartService.list(wr);
    }

    @GetMapping("/gridList")
    public List<ImageChart> getGrid() {
        QueryWrapper<ImageChart> wr = new QueryWrapper<>();
        wr.eq("func_type","grid");
        return imageChartService.list(wr);
    }

    @GetMapping("/companyDetails")
    public List<ImageChart> getCompanyDetails() {
        QueryWrapper<ImageChart> wr = new QueryWrapper<>();
        wr.o
        wr.eq("func_type","companyDetails");
        return imageChartService.list(wr);
    }

}
