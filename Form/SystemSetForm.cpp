#include "SystemSetForm.h"
#include "ui_SystemSetForm.h"
#include "Mgr/FormFrame.h"
#include "Utils/ButtonInfo.h"
#include "Utils/WidgetStyle.h"
#include "Utils/QJsonAnalysis.h"
#include "Utils/UserMsgBox.h"
#include "Utils/SystemConstants.h"

SystemSetForm::SystemSetForm(QWidget* parent) : QWidget(parent), ui(new Ui::SystemSetWidget)
{
    ui->setupUi(this);
    ui->tabWidget->tabBar()->setStyle(new CustomTabStyle);
}

SystemSetForm::~SystemSetForm()
{
    delete ui;
}

// [slot函数]退出按键
void SystemSetForm::on_pushButton_Esc_clicked()
{
    FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_systemsetform_);
    delete FormFrame::GetInstance()->p_systemsetform_;
    FormFrame::GetInstance()->p_systemsetform_ = nullptr;
    FormFrame::GetInstance()->formstacked_id_.systemsetform_id = -1;
    FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.mainform_id);
}

// [slot函数]控件槽函数



