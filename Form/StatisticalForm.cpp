#include "StatisticalForm.h"
#include "ui_StatisticalForm.h"
#include "Mgr/FormFrame.h"
#include "Utils/WidgetStyle.h"
#include "Utils/SystemConstants.h"
#include "Utils/SystemUtils.h"

ButtonStatisticalInfo CameraStatisticalInfo[CAMERA_NUM];

StatisticalForm::StatisticalForm(QWidget *parent) : QWidget(parent), ui(new Ui::StatisticalWidget)
{
    ui->setupUi(this);
    ui->tabWidget->tabBar()->setStyle(new CustomTabStyle);
    chartview_ = nullptr;
    p_buttonres = nullptr;
    buttonname_ = SystemUtils::GetNameForButtonConfigFile();
    ShowCountChartInfo(1);
    if(buttonname_ == "")
    {
        ui->checkBox_lssj1->setEnabled(false);
        ui->checkBox_dqsj1->setEnabled(false);
        ui->checkBox_lssj2->setEnabled(false);
        ui->checkBox_dqsj2->setEnabled(false);
    }
    else
    {
        ui->checkBox_lssj2->setEnabled(true);
        ui->checkBox_dqsj2->setEnabled(true);
        ui->checkBox_lssj2->setEnabled(true);
        ui->checkBox_dqsj2->setEnabled(true);

        // 解析历史数据json文件
        QString pathname(SystemUtils::GetPathForButtonConfigFile() + buttonname_ + "/" + buttonname_ + "STA.ini");
        p_buttonres = new QJsonAnalysis(pathname, true);
    }
}

StatisticalForm::~StatisticalForm()
{
    if(p_buttonres != nullptr)
    {
        delete p_buttonres;
        p_buttonres = nullptr;
    }
    if(chartview_ != nullptr)
    {
        delete chartview_;
        chartview_ = nullptr;
    }
    delete ui;
}

void StatisticalForm::ShowCountChartInfo(quint32 type)
{
    if(chartview_ != nullptr)
    {
        delete chartview_;
        chartview_ = nullptr;
    }
    chartview_ = new QChart();
    QBarSeries* series = new QBarSeries(chartview_);
    QBarSet* set = new QBarSet("SLXX", chartview_);
    QBarCategoryAxis* axis = new QBarCategoryAxis(chartview_);
    // 数据
    *set << 1 << 2 << 3 << 4 << 5 << 6;
    series->append(set);
    series->setLabelsVisible(true);
    series->setLabelsPosition(QAbstractBarSeries::LabelsCenter);
    // 坐标轴图例
    QStringList categories;
    categories << "相机1正品" << "相机1次品" << "相机2正品" << "相机2次品" << "相机3正品" << "相机3次品";
    axis->append(categories);
    // 显示
    chartview_->addSeries(series);
    chartview_->legend()->setVisible(false);
    chartview_->legend()->setAlignment(Qt::AlignBottom);
    chartview_->setAnimationOptions(QChart::NoAnimation);
    chartview_->createDefaultAxes();
    chartview_->setAxisX(axis, series);
    ui->widget->setRenderHint(QPainter::Antialiasing);
    ui->widget->setChart(chartview_);
}

void StatisticalForm::ShowDefectChartInfo(quint32 type)
{

}

// [Slot]返回触发
void StatisticalForm::on_pushButton_Esc_clicked()
{
    // 清除当前Form
    FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_statisticalform_);
    delete FormFrame::GetInstance()->p_statisticalform_;
    FormFrame::GetInstance()->p_statisticalform_ = nullptr;
    FormFrame::GetInstance()->formstacked_id_.statisticalform_id = -1;
    FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.mainform_id);
}

// [Slot]保存图片触发
void StatisticalForm::on_pushButton_SavePic_clicked()
{

}

// [Slot]数量统计信息（当前数据）触发
void StatisticalForm::on_checkBox_dqsj1_clicked(bool checked)
{
    if(checked)
        ui->checkBox_lssj1->setChecked(false);
}

// [Slot]缺陷统计信息（当前数据）触发
void StatisticalForm::on_checkBox_lssj1_clicked(bool checked)
{
    if(checked)
        ui->checkBox_dqsj1->setChecked(false);
}
