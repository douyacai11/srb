package com.atguigu.srb.core.listener;

//读写 Excel文件的监听器

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.srb.core.mapper.DictMapper;
import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {

    //每5条记录 批量存储一次数据
    public static final int BATCH_COUNT=5;

     ArrayList<ExcelDictDTO> list = new ArrayList<>();

     private DictMapper dictMapper;

     //有参构造函数
    public ExcelDictDTOListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(ExcelDictDTO data, AnalysisContext context) {
        log.info("解析到一条记录: {}"+ data);

        //批量保存数据到list中
        list.add(data);
        if (list.size()>=BATCH_COUNT){
            saveData();
            list.clear();
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //当剩余记录树不足5条的数据 会被这里存储到数据库中
        saveData();
        log.info("所有数据解析完成！");
    }

    private void saveData(){
     log.info("{} 条数据 存储到数据库.......",list.size());



     //调用mapper层的方法存储到数据库 save list对象
     dictMapper.insertBatch(list);

     log.info("{} 条数据 存储到数据库成功！",list.size());
    }
}
