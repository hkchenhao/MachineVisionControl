#include "MachineLearnForm.h"
#include "ui_MachineLearnForm.h"
#include "Mgr/FormFrame.h"
#include "Utils/WidgetStyle.h"
#include "Utils/QJsonAnalysis.h"

// 基本信息选择列表（材质-形状-线孔数-透明性-花色-主色）
QPair<QString, qint32> ButtonMaterialList[] = {{"resin",0}, {"fruit",1}, {"shell",2}, {"metal",3}, {"jade",4}};
QPair<QString, qint32> ButtonShapeList[] = {{"circle",0}, {"heart",1}, {"triangle",2}, {"square",3}, {"pentagon",4}, {"quincunx",5}, {"other",6}};
QPair<QString, qint32> ButtonHoleNumList[] = {{"2",0}, {"4",1}};
QPair<QString, qint32> ButtonLightList[] = {{"transparent",0}, {"semiTransparent",1}, {"none",2}};
QPair<QString, qint32> ButtonPatternList[] = {{"solidColor",0}, {"multiColor",1}, {"texture",2}, {"character",3}, {"picture",4}};
QPair<QString, qint32> ButtonColorList[] = {{"white",0}, {"red",1}, {"orange",2}, {"yellow",3}, {"green",4}, {"cyan",5}, {"blue",6}, {"purple",7}, {"black",8}};

MachineLearnForm::MachineLearnForm(QWidget *parent) : QWidget(parent), ui(new Ui::MachineLearnWidget)
{
    ui->setupUi(this);
    ui->tabWidget->tabBar()->setStyle(new CustomTabStyle);
}

MachineLearnForm::~MachineLearnForm()
{
    delete ui;
}


// [slot函数]保存按键
void MachineLearnForm::on_pushButton_Save_clicked()
{
    // 创建JSON文件
    QString jsonparentstr;
    QJsonAnalysis infojson("{}", false);
    // JSON时间信息
    infojson.set("time", QDate::currentDate().toString("yyyy-MM-dd"));
    // JSON纽扣正面基本信息
    jsonparentstr = "InfoFront.";
    infojson.set(jsonparentstr+"materialF", ButtonMaterialList[ui->box_cz_0->currentIndex()].first);
    infojson.set(jsonparentstr+"sizeF", ui->line_cc->text());
    infojson.set(jsonparentstr+"shapeF", ButtonShapeList[ui->box_xz_0->currentIndex()].first);
    infojson.set(jsonparentstr+"holeNumF", ButtonHoleNumList[ui->box_xks_0->currentIndex()].first);
    infojson.set(jsonparentstr+"lightF", ButtonLightList[ui->box_tmx_0->currentIndex()].first);
    infojson.set(jsonparentstr+"patternF", ButtonPatternList[ui->box_hs_0->currentIndex()].first);
    infojson.set(jsonparentstr+"colorF", ButtonColorList[ui->box_zs_0->currentIndex()].first);
    // JSON纽扣反面基本信息
    jsonparentstr = "InfoBack.";
    infojson.set(jsonparentstr+"materialB", ButtonMaterialList[ui->box_cz_1->currentIndex()].first);
    infojson.set(jsonparentstr+"sizeB", ui->line_cc->text());
    infojson.set(jsonparentstr+"shapeB", ButtonShapeList[ui->box_xz_1->currentIndex()].first);
    infojson.set(jsonparentstr+"holeNumB", ButtonHoleNumList[ui->box_xks_1->currentIndex()].first);
    infojson.set(jsonparentstr+"lightB", ButtonLightList[ui->box_tmx_1->currentIndex()].first);
    infojson.set(jsonparentstr+"patternB", ButtonPatternList[ui->box_hs_1->currentIndex()].first);
    infojson.set(jsonparentstr+"colorB", ButtonColorList[ui->box_zs_1->currentIndex()].first);

    infojson.save("info.json");
    ui->box_cz_0->setCurrentIndex(1);
}
// [slot函数]修改按键
void MachineLearnForm::on_pushButton_Revise_clicked()
{

}
// [slot函数]退出按键
void MachineLearnForm::on_pushButton_Esc_clicked()
{
    // 清除当前Form
    FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_machinelearnform_);
    delete FormFrame::GetInstance()->p_machinelearnform_;
    FormFrame::GetInstance()->p_machinelearnform_ = nullptr;
    FormFrame::GetInstance()->formstacked_id_.machinelearnform_id = -1;
    FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.mainform_id);
}
