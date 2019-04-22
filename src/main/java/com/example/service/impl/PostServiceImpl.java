package com.example.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Post;
import com.example.mapper.PostMapper;
import com.example.service.PostService;
import com.example.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mzc
 * @since 2019-04-22
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void initIndexWeekRank() {
        List<Post> last7DayPosts = this.list(new QueryWrapper<Post>()
                .ge("created", DateUtil.offsetDay(new Date(), -7).toJdkDate())
                .select("id, title, user_id, comment_count, view_count, created"));

        for (Post post : last7DayPosts) {
            String key = "day_rank:" + DateUtil.format(post.getCreated(), DatePattern.PURE_DATE_PATTERN);
            redisUtil.zSet(key, post.getId(), post.getCommentCount());

            long betweenDay = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
            long expireTime = (7 - betweenDay) * 24 * 60 * 60;
            redisUtil.expire(key, expireTime);

            //缓存文章基本信息（hash结构）
            this.hashCachePostIdAndTitle(post);
        }
        //7天阅读相加。
        this.zUnionAndStoreLast7DaysForLastWeekRank();
    }

    /**
     * 把最近7天的文章评论数量统计一下
     * 用于首页的7天评论排行榜
     */
    public void zUnionAndStoreLast7DaysForLastWeekRank() {
        String prifix = "day_rank:";
        List<String> keys  = new ArrayList<>();
        String key = prifix + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        for(int i = -6 ; i < 0; i++) {
            Date date = DateUtil.offsetDay(new Date(), i).toJdkDate();
            keys.add(prifix + DateUtil.format(date, DatePattern.PURE_DATE_PATTERN));
        }
        redisUtil.zUnionAndStore(key, keys, "last_week_rank");
    }

    /**
     * hash结构缓存文章标题和id
     * @param post
     */
    private void hashCachePostIdAndTitle(Post post) {
        boolean exit = redisUtil.hasKey("rank_post_" + post.getId());
        if (!exit) {
            long betweenDay = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
            long expire = (7 - betweenDay) * 24 * 60 * 60;
            redisUtil.hset("rank_post_" + post.getId(), "post:id", post.getId(), expire);
            redisUtil.hset("rank_post_" + post.getId(), "post:title", post.getTitle(), expire);
        }
    }
}
