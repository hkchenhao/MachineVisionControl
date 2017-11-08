#include "MachineLearnForm.h"
#include "ui_MachineLearnForm.h"
#include "Mgr/FormFrame.h"

MachineLearnForm::MachineLearnForm(QWidget *parent) : QWidget(parent), ui(new Ui::MachineLearnWidget)
{
    ui->setupUi(this);
    ui->tabWidget->tabBar()->setStyle(new CustomTabStyle1);
}

MachineLearnForm::~MachineLearnForm()
{
    delete ui;
}

void MachineLearnForm::on_pushButton_Esc_clicked()
{
    // 清除当前Form
    FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_machinelearnform_);
    delete FormFrame::GetInstance()->p_machinelearnform_;
    FormFrame::GetInstance()->p_machinelearnform_ = nullptr;
    FormFrame::GetInstance()->formstacked_id_.machinelearnform_id = -1;
    FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.mainform_id);
}
