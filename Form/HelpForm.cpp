#include "HelpForm.h"
#include "ui_HelpForm.h"
#include "Mgr/FormFrame.h"
#include "Utils/WidgetStyle.h"
#include "Utils/UserMsgBox.h"
#include "Utils/SystemConstants.h"

HelpForm::HelpForm(QWidget* parent) : QWidget(parent), ui(new Ui::HelpWidget)
{
    ui->setupUi(this);
}

HelpForm::~HelpForm()
{
    delete ui;
}

// [slot函数]退出按键
void HelpForm::on_pushButton_Esc_clicked()
{
    FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_helpform_);
    delete FormFrame::GetInstance()->p_helpform_;
    FormFrame::GetInstance()->p_helpform_ = nullptr;
    FormFrame::GetInstance()->formstacked_id_.helpform_id = -1;
    FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.mainform_id);
}

// [slot函数]控件槽函数



