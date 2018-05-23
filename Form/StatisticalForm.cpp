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
    chartview_slxx = nullptr;
    chartview_qxxx = nullptr;
    p_buttonres = nullptr;
    buttonname_ = SystemUtils::GetNameForButtonConfigFile();
    // 解析历史数据json文件
    if(buttonname_ != "")
    {
        QString pathname(SystemUtils::GetPathForButtonConfigFile() + buttonname_ + "/" + buttonname_ + "STA.ini");
        p_buttonres = new QJsonAnalysis(pathname, true);
    }

//    ShowCountChartInfo(1);
//    ShowDefectChartInfo(1);

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
    }
}

StatisticalForm::~StatisticalForm()
{
    if(p_buttonres != nullptr)
    {
        delete p_buttonres;
        p_buttonres = nullptr;
    }
    if(chartview_slxx != nullptr)
    {
        delete chartview_slxx;
        chartview_slxx = nullptr;
    }
    if(chartview_qxxx != nullptr)
    {
        delete chartview_qxxx;
        chartview_qxxx = nullptr;
    }
    delete ui;
}

void StatisticalForm::ShowCountChartInfo(quint32 type)
{
    if(chartview_slxx != nullptr)
    {
        delete chartview_slxx;
        chartview_slxx = nullptr;
    }
    chartview_slxx = new QChart();
    QBarSeries* series = new QBarSeries(chartview_slxx);
    QBarSet* set = new QBarSet("SLXX", chartview_slxx);
    QBarCategoryAxis* axis = new QBarCategoryAxis(chartview_slxx);
    QChartView* chartView = new QChartView(chartview_slxx);
    // 数据
    if(type == 1)
    {
        if(p_buttonres != nullptr)
            *set << p_buttonres->getInt("count.pos1S") << p_buttonres->getInt("count.neg1S")
                 << p_buttonres->getInt("count.pos2S") << p_buttonres->getInt("count.neg2S")
                 << p_buttonres->getInt("count.pos3S") << p_buttonres->getInt("count.neg3S");
    }
    else
    {
        *set << 1 << 2 << 3 << 4 << 5 << 6;
    }
    series->append(set);
    series->setLabelsVisible(true);
    series->setLabelsPosition(QAbstractBarSeries::LabelsCenter);
    // 坐标轴图例
    QStringList categories;
    categories << "相机1正品" << "相机1次品" << "相机2正品" << "相机2次品" << "相机3正品" << "相机3次品";
    axis->append(categories);
    // 显示
    chartview_slxx->addSeries(series);
    chartview_slxx->legend()->setVisible(false);
    chartview_slxx->legend()->setAlignment(Qt::AlignBottom);
    chartview_slxx->setAnimationOptions(QChart::NoAnimation);
    chartview_slxx->createDefaultAxes();
    chartview_slxx->setAxisX(axis, series);
    chartView->setRenderHint(QPainter::Antialiasing);
    ui->gridLayout_slxx->addWidget(chartView, 1, 0);
    ui->widget_slxx->setLayout(ui->gridLayout_slxx);

    //ui->widget_slxx->setRenderHint(QPainter::Antialiasing);
    //ui->widget_slxx->setChart(chartview_slxx);
}

void StatisticalForm::ShowDefectChartInfo(quint32 type)
{
    //unfiS":30,"edgeS":491,"foreS":0,"holeS":6880,"othrS":0,"blobS":0,"pitsS":0,"geomS:4778
    qint32 totalcount = 0;
    QMap<QString, QString> bufname = {{"unfiS", "不均匀"}, {"edgeS", "崩边"}, {"foreS", "X"}, {"holeS", "线孔变形"},
                                      {"othrS", "其它"}, {"blobS", "X"}, {"pitsS", "X"}, {"geomS", "几何偏差"}};
    QMap<QString, qint32> bufcount;

    if(chartview_qxxx != nullptr)
    {
        delete chartview_qxxx;
        chartview_qxxx = nullptr;
    }
    chartview_qxxx = new QChart();
    QPieSeries* series = new QPieSeries(chartview_qxxx);
    QChartView* chartView = new QChartView(chartview_qxxx);
    // 数据
    if(type == 1)
    {
        if(p_buttonres != nullptr)
        {
            totalcount += bufcount["unfiS"] = p_buttonres->getInt("flaw.unfiS");
            totalcount += bufcount["edgeS"] = p_buttonres->getInt("flaw.edgeS");
            totalcount += bufcount["foreS"] = p_buttonres->getInt("flaw.foreS");
            totalcount += bufcount["holeS"] = p_buttonres->getInt("flaw.holeS");
            totalcount += bufcount["blobS"] = p_buttonres->getInt("flaw.blobS");
            totalcount += bufcount["pitsS"] = p_buttonres->getInt("flaw.pitsS");
            totalcount += bufcount["geomS"] = p_buttonres->getInt("flaw.geomS");
            totalcount += bufcount["othrS"] = p_buttonres->getInt("flaw.othrS");
            for(auto it = bufcount.begin(); it != bufcount.end(); ++it)
            {
                if(it.value() != 0)
                {
                    double per = it.value() / (double)totalcount * 100;
                    series->append(bufname[it.key()] + QString::number(per, 'f', 2) + "%", it.value());
                }
            }
        }
    }
    else
    {
        series->append("Jane", 1);
        series->append("Joe", 2);
        series->append("Andy", 3);
        series->append("Barbara", 4);
        series->append("Axel", 5);
    }
    // 显示
    series->setLabelsVisible(true);
    chartview_qxxx->addSeries(series);
    chartview_qxxx->legend()->setVisible(false);
    chartview_qxxx->legend()->setAlignment(Qt::AlignBottom);
    chartview_qxxx->setAnimationOptions(QChart::NoAnimation);
    chartView->setRenderHint(QPainter::Antialiasing);
    ui->gridLayout_qxxx->addWidget(chartView, 1, 0);
    ui->widget_qxxx->setLayout(ui->gridLayout_qxxx);
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
    {
        ui->checkBox_lssj1->setChecked(false);
        ShowCountChartInfo(0);
    }
}

// [Slot]数量统计信息（历史数据）触发
void StatisticalForm::on_checkBox_lssj1_clicked(bool checked)
{
    if(checked)
    {
        ui->checkBox_dqsj1->setChecked(false);
        ShowCountChartInfo(1);
    }
}

// [Slot]缺陷统计信息（当前数据）触发
void StatisticalForm::on_checkBox_dqsj2_clicked(bool checked)
{
    if(checked)
    {
        ui->checkBox_lssj2->setChecked(false);
        ShowDefectChartInfo(0);
    }
}

// [Slot]缺陷统计信息（历史数据）触发
void StatisticalForm::on_checkBox_lssj2_clicked(bool checked)
{
    if(checked)
    {
        ui->checkBox_dqsj2->setChecked(false);
        ShowDefectChartInfo(1);
    }
}
