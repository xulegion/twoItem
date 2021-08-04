package com.yc.service;

import com.yc.bean.Bussiness;
import com.yc.bean.PageBean;
import com.yc.dao.DBHelper;
import com.yc.dao.MyProperties;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author CH
 * 分页操作
 */
public class ResfoodBizImpl implements ResfoodBiz {

    private DBHelper db=new DBHelper();
    /**
     * @param pagenum 第几页
     * @param pagesize 每页的大小
     */
    public PageBean<Bussiness> findResfoodByPage(int pagenum , int pagesize,String location ){
        PageBean<Bussiness> pageBean=new PageBean<>();
        if(pagenum<=0){
            pagenum=1;
        }
        if(pagesize<1){
            pagesize=4;
        }
        int start=(pagenum-1)*pagesize; //第几条开始
        String sql="select b.fid ,b.location,b.fname,b.detail,b.fphoto from bussiness as b\n" +
                "left join\n" +
                "(select fid,count(fid) num from comment group by fid) as a on a.fid=b.fid where b.location=? order by a.num  desc limit "+ start+","+pagesize;

        List<Bussiness> bussinessList = db.getForList(Bussiness.class, sql, location);

        String sql2="select count(*) as totals from bussiness where location=?";
        List<Map<String, String>> totalMap = db.doSelect(sql2,location);
        int totals = Integer.parseInt(totalMap.get(0).get("totals"));

        MyProperties properties = MyProperties.getInstance();
        Jedis jedis = new Jedis(properties.getProperty("redis_host"), Integer.parseInt(properties.getProperty("redis_port")));
        List<Long> scardList=new ArrayList<>();
        for (Bussiness bussiness : bussinessList) {
            Integer fid = bussiness.getFid();
            Long scard = jedis.scard(fid+"" );
            scardList.add(scard);
        }


        pageBean.setPagenum(pagenum);
        pageBean.setPagesize(pagesize);
        pageBean.setTotals(totals);
        pageBean.setData(bussinessList);
        pageBean.getTotalpages();
        pageBean.getNextpage();
        pageBean.getPrepage();

        pageBean.setScardList( scardList );

        return pageBean;
    }


}
