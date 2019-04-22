package com.example.service;

import com.example.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mzc
 * @since 2019-04-22
 */
public interface PostService extends IService<Post> {

    /**
     * 初始化首页的周评论排行榜
     */
    public void initIndexWeekRank();
}
