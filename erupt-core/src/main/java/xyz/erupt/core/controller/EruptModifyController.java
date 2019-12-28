package xyz.erupt.core.controller;import com.google.gson.Gson;import com.google.gson.JsonObject;import lombok.extern.java.Log;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.web.bind.annotation.*;import xyz.erupt.annotation.fun.DataProxy;import xyz.erupt.core.annotation.EruptRouter;import xyz.erupt.core.bean.EruptApiModel;import xyz.erupt.core.bean.EruptModel;import xyz.erupt.core.constant.RestPath;import xyz.erupt.core.exception.EruptNoLegalPowerException;import xyz.erupt.core.service.CoreService;import xyz.erupt.core.service.DataService;import xyz.erupt.core.util.AnnotationUtil;import xyz.erupt.core.util.DataHandlerUtil;import xyz.erupt.core.util.EruptSpringUtil;import xyz.erupt.core.util.EruptUtil;import javax.transaction.Transactional;/** * Erupt 对数据的增删改查 * Created by liyuepeng on 9/28/18. */@RestController@RequestMapping(RestPath.ERUPT_DATA)@Logpublic class EruptModifyController {    @Autowired    private Gson gson;    private void log(EruptModel eruptModel, String placeholder, String content) {        log.info("[" + eruptModel.getEruptName() + " -> " + placeholder + "]:" + content);    }    @PostMapping("/{erupt}")    @ResponseBody    @EruptRouter(authIndex = 1)    public EruptApiModel addEruptData(@PathVariable("erupt") String erupt, @RequestBody JsonObject data) {        EruptModel eruptModel = CoreService.getErupt(erupt);        this.log(eruptModel, "add", data.toString());        EruptApiModel eruptApiModel = EruptUtil.validateEruptValue(eruptModel, data);        if (eruptApiModel.getStatus() == EruptApiModel.Status.ERROR) return eruptApiModel;        if (eruptModel.getErupt().power().add()) {            Object obj = gson.fromJson(gson.toJson(data), eruptModel.getClazz());            DataHandlerUtil.clearObjectDefaultValueByJson(obj, data);            for (Class<? extends DataProxy> proxy : eruptModel.getErupt().dataProxy()) {                EruptSpringUtil.getBean(proxy).beforeAdd(obj);            }            AnnotationUtil.getEruptDataProcessor(eruptModel.getClazz()).addData(eruptModel, obj);            for (Class<? extends DataProxy> proxy : eruptModel.getErupt().dataProxy()) {                EruptSpringUtil.getBean(proxy).afterAdd(obj);            }            return EruptApiModel.successApi();        } else {            throw new EruptNoLegalPowerException();        }    }    @PutMapping("/{erupt}")    @ResponseBody    @EruptRouter(authIndex = 1)    public EruptApiModel editEruptData(@PathVariable("erupt") String erupt, @RequestBody JsonObject data) {        EruptModel eruptModel = CoreService.getErupt(erupt);        EruptApiModel eruptApiModel = EruptUtil.validateEruptValue(eruptModel, data);        if (eruptApiModel.getStatus() == EruptApiModel.Status.ERROR) return eruptApiModel;        if (eruptModel.getErupt().power().edit()) {            Object obj = this.gson.fromJson(data.toString(), eruptModel.getClazz());            DataHandlerUtil.clearObjectDefaultValueByJson(obj, data);            for (Class<? extends DataProxy> proxy : eruptModel.getErupt().dataProxy()) {                EruptSpringUtil.getBean(proxy).beforeUpdate(obj);            }            AnnotationUtil.getEruptDataProcessor(eruptModel.getClazz()).editData(eruptModel, obj);            this.log(eruptModel, "edit", data.toString());            for (Class<? extends DataProxy> proxy : eruptModel.getErupt().dataProxy()) {                EruptSpringUtil.getBean(proxy).afterUpdate(obj);            }            return EruptApiModel.successApi();        } else {            throw new EruptNoLegalPowerException();        }    }    @DeleteMapping("/{erupt}/{id}")    @ResponseBody    @EruptRouter(authIndex = 1)    public EruptApiModel deleteEruptData(@PathVariable("erupt") String erupt, @PathVariable("id") String id) {        EruptModel eruptModel = CoreService.getErupt(erupt);        if (eruptModel.getErupt().power().delete()) {            DataService dataService = AnnotationUtil.getEruptDataProcessor(eruptModel.getClazz());            Object eid = EruptUtil.toEruptId(eruptModel, id);            //获取对象数据信息用于DataProxy函数中            Object obj = dataService.findDataById(eruptModel, eid);            for (Class<? extends DataProxy> proxy : eruptModel.getErupt().dataProxy()) {                EruptSpringUtil.getBean(proxy).beforeDelete(obj);            }            dataService.deleteData(eruptModel, eid);            this.log(eruptModel, "delete", id);            for (Class<? extends DataProxy> proxy : eruptModel.getErupt().dataProxy()) {                EruptSpringUtil.getBean(proxy).afterDelete(obj);            }            return EruptApiModel.successApi();        } else {            throw new EruptNoLegalPowerException();        }    }    //为了事务性考虑所以增加了批量删除功能    @Transactional    @DeleteMapping("/{erupt}")    @ResponseBody    @EruptRouter(authIndex = 1)    public EruptApiModel deleteEruptDataList(@PathVariable("erupt") String erupt, @RequestParam("ids") String[] ids) {        EruptApiModel eruptApiModel = EruptApiModel.successApi();        for (String id : ids) {            eruptApiModel = this.deleteEruptData(erupt, id);            if (eruptApiModel.getStatus() == EruptApiModel.Status.ERROR) {                break;            }        }        return eruptApiModel;    }}