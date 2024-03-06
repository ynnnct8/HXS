package com.hmdp.service.impl;

import ch.qos.logback.core.util.TimeUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {
        String key = CACHE_SHOP_TYPE_KEY + UUID.randomUUID(true);
        // 先从redis中查询商铺缓存
        String shopTypeJson = stringRedisTemplate.opsForValue().get(key);
        // 判断是否有缓存
        if (StrUtil.isNotBlank(shopTypeJson)) {
            // 有缓存则直接返回
            List<ShopType> typeList = JSONUtil.toList(shopTypeJson, ShopType.class);
            return Result.ok(typeList);
        }
        // 没有缓存则查询数据库
        List<ShopType> typeList = this.list(new LambdaQueryWrapper<ShopType>().orderByAsc(ShopType::getSort));
        // 不存在则返回失败
        if (typeList == null) {
            return Result.fail("商铺类型不存在");
        }
        // 存在则将商铺数据写入缓存
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(typeList),
                CACHE_SHOP_TYPE_TTL, TimeUnit.MINUTES);
        return Result.ok(typeList);
    }
}
