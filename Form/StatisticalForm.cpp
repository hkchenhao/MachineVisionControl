#include "StatisticalForm.h"
#include "ui_StatisticalForm.h"
#include "Mgr/FormFrame.h"

StatisticalForm::StatisticalForm(QWidget *parent) : QWidget(parent), ui(new Ui::StatisticalWidget)
{
    ui->setupUi(this);
    ui->tabWidget->tabBar()->setStyle(new CustomTabStyle);

    QLineSeries* line1 = new QLineSeries();
    for(double x=0;x<10;x+=0.1)
    {
        line1->append(x,sin(x));
    }
    QChart* c = new QChart();
    c->addSeries(line1);
    ui->widget->setChart(c);
}

StatisticalForm::~StatisticalForm()
{
    delete ui;
}

void StatisticalForm::on_pushButton_Esc_clicked()
{
    // 清除当前Form
    FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_statisticalform_);
    delete FormFrame::GetInstance()->p_statisticalform_;
    FormFrame::GetInstance()->p_statisticalform_ = nullptr;
    FormFrame::GetInstance()->formstacked_id_.statisticalform_id = -1;
    FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.mainform_id);
}
