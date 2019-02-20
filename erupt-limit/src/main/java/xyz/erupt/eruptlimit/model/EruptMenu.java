package xyz.erupt.eruptlimit.model;

import lombok.Data;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.sub_erupt.Tree;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.EditType;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.sub_edit.*;

import javax.persistence.*;

/**
 * Created by liyuepeng on 11/22/18.
 */
@Entity
@Table(name = "E_MENU")
@Erupt(
        name = "菜单配置",
        sort = "sort",
        tree = @Tree(id = "id", label = "name", pid = "parentMenu.id")
)
@Data
public class EruptMenu extends BaseModel {

    @Column(name = "NAME")
    @EruptField(
            views = @View(title = "名称"),
            edit = @Edit(
                    title = "名称",
                    notNull = true
            )
    )
    private String name;

    @Column(name = "PATH")
    @EruptField(
            edit = @Edit(
                    title = "地址",
                    inputType = @InputType(
                            prefix = {
                                    @VL(value = "'/build/table/'", label = "TABLE"),
                                    @VL(value = "'/build/tree/'", label = "TREE"),
                                    @VL(value = "'/build/report/'", label = "REPORT"),
                                    @VL(value = "'/'", label = "'/'"),
                            }
                    )
            )
    )
    private String path;

    @Column(name = "SORT")
    @EruptField(
            edit = @Edit(
                    title = "顺序"
            )
    )
    private Integer sort;


    @Column(name = "IS_SHOW")
    @EruptField(
            edit = @Edit(
                    title = "是否显示",
                    type = EditType.BOOLEAN,
                    boolType = @BoolType(trueText = "显示", falseText = "隐藏", defaultValue = true)
            )
    )
    private Boolean show;

    @Column(name = "STATUS")
    @EruptField(
            edit = @Edit(
                    title = "菜单状态",
                    type = EditType.BOOLEAN,
                    boolType = @BoolType(trueText = "可用", falseText = "禁用", defaultValue = true)
            )
    )
    private Boolean status;


    @ManyToOne
    @JoinColumn(name = "PARENT_MENU_ID")
    @EruptField(
            edit = @Edit(
                    title = "上级菜单",
                    type = EditType.REFERENCE,
                    referenceType = @ReferenceType(id = "id", label = "name")
            )
    )
    private EruptMenu parentMenu;

    @Column(name = "ICON")
    @EruptField(
            edit = @Edit(
                    title = "图标",
                    desc = "请参考图标库font-awesome（图标仅会在最父级节点中展示）"
            )
    )
    private String icon;

    @Column(name = "TARGET")
    @EruptField(
            edit = @Edit(
                    title = "打开方式",
                    type = EditType.CHOICE,
                    choiceType = @ChoiceType(vl = {
                            @VL(value = "0", label = "_target"),
                            @VL(value = "1", label = "_self")
                    }, type = ChoiceEnum.RADIO)
            )
    )
    private Integer target;

    @Column(name = "REMARK")
    @EruptField(
            edit = @Edit(
                    title = "功能描述",
                    inputType = @InputType(type = InputEnum.TEXTAREA, length = 255)
            )
    )
    private String remark;

}