package com.yc.dao;


import com.yc.bean.Bussiness;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BussinessDao {
    //索引存放地址
    String path;

    public List<Bussiness> queryBussiness() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        DBHelper db=new DBHelper();
        ResultSet resultSet = null;
        List<Bussiness> products = db.getForList(Bussiness.class,"select *from bussiness");

        return products;

    }

   @Test
    public void createIndex(HttpSession session) throws Exception {

        //在执行采集数据之前先对文档进行删除
       this.deleteByCondition(session);
        // 采集数据
        BussinessDao dao= new BussinessDao();
        List<Bussiness> products = dao.queryBussiness();

        // 将采集到的数据封装到Document对象中
        List<Document> docList = new ArrayList<>();
        for (Bussiness product : products) {
            Document document = new Document();

            // store:如果是yes，则说明存储到文档域中
            Field fid = new TextField("fid", product.getFid().toString(), Field.Store.YES);
            Field fname = new TextField("fname", product.getFname(), Field.Store.YES);
            Field detail = new TextField("detail", product.getDetail(), Field.Store.YES);
            Field fphoto = new TextField("fphoto", product.getFphoto(), Field.Store.YES);
            Field cid= new TextField("cid", String.valueOf(product.getCid()), Field.Store.YES);
            Field price = new TextField("price", String.valueOf(product.getPrice()), Field.Store.YES);
            Field location = new TextField("location", product.getLocation(), Field.Store.YES);
            // 将field域设置到Document对象中
            document.add(fid);
            document.add(fname);
            document.add(detail);
            document.add(fphoto);
            document.add(cid);
            document.add(price);
            document.add(location);

            docList.add(document);
        }

        // 创建分词器，标准分词器
       Analyzer analyzer = new StandardAnalyzer();

       // 创建IndexWriter
       IndexWriterConfig config = new IndexWriterConfig(analyzer);
       // 指定索引库的地址
         path=session.getServletContext().getRealPath("middle/");
       System.out.println(path);
       Path indexFile = Paths.get(path);
       Directory directory = FSDirectory.open(indexFile);
       IndexWriter indexWriter = new IndexWriter(directory, config);

       // 通过IndexWriter对象将Document写入到索引库中
       for (Document document : docList) {
           indexWriter.addDocument(document);
       }

       // 关闭indexWriter
       indexWriter.close();

    }


    @Test
    public  List<Bussiness> searchIndex(String inputValue) throws Exception {
        // 创建query对象
        // 使用QueryParser搜索时，需要指定分词器，搜索时的分词器要和索引时的分词器一致
        // 第一个参数：默认搜索的域的名称
        List<Bussiness> result=new ArrayList<>();
        String[] fields = {"fname", "detail","location"};
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());



        // 通过queryparser来创建query对象
        // 参数：输入的lucene的查询语句(关键字一定要大写)
        Query query = parser.parse(inputValue);

        // 创建IndexSearcher
        // 指定索引库的地址

        Path indexFile = Paths.get(path);
        Directory directory = FSDirectory.open(indexFile);
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(reader);

        // 通过searcher来搜索索引库
        // 第二个参数：指定需要显示的顶部记录的N条
        TopDocs topDocs = indexSearcher.search(query, 5);

        // 根据查询条件匹配出的记录
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (ScoreDoc scoreDoc : scoreDocs) {
            // 获取文档的ID
            int docId = scoreDoc.doc;

            // 通过ID获取文档
            Document doc = indexSearcher.doc(docId);
            Bussiness bus=new Bussiness();
            bus.setFid(Integer.parseInt(doc.get("fid")));
           bus.setFname(doc.get("fname"));
          bus.setDetail(doc.get("detail"));
          bus.setFphoto(doc.get("fphoto"));
          bus.setLocation(doc.get("location"));
          bus.setPrice(new BigDecimal(doc.get("price")));
        bus.setCid(Integer.valueOf(doc.get("cid")));
        result.add(bus);
        }
        reader.close();
        return result;
        // 关闭资源

    }


    @Test
    public void updateIndex() throws Exception {
        // 创建分词器，标准分词器
        Analyzer analyzer = new StandardAnalyzer();
        // 创建IndexWriter
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Path indexFile = Paths.get(path);
        Directory directory = FSDirectory.open(indexFile);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // 第一个参数：指定查询条件
        // 第二个参数：修改之后的对象
        // 修改时如果根据查询条件可以查出结果，则将其删掉，并进行覆盖新的doc；否则直接新增一个doc
        Document doc = new Document();
        doc.add(new TextField("fname","pingpong", Field.Store.YES));
        indexWriter.updateDocument(new Term("ffname", "xiaoming"), doc);

        indexWriter.close();
    }

    @Test
    public void deleteByCondition(HttpSession session) throws Exception {
        // 创建分词器，标准分词器
        Analyzer analyzer = new StandardAnalyzer();
        // 创建IndexWriter
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        System.out.println("path"+path);
        path=session.getServletContext().getRealPath("middle/");
        Path indexFile = Paths.get(path);
        Directory directory = FSDirectory.open(indexFile);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // Terms
        indexWriter.deleteAll();
        indexWriter.close();
    }

}