#include "FileManagerForm.h"
#include "ui_FileManagerForm.h"
#include "Mgr/FormFrame.h"
#include "Utils/ButtonInfo.h"
#include "Utils/WidgetStyle.h"
#include "Utils/QJsonAnalysis.h"
#include "Utils/UserMsgBox.h"
#include "Utils/SystemConstants.h"

FileManagerForm::FileManagerForm(QWidget* parent) : QWidget(parent), ui(new Ui::FileManagerWidget)
{
    ui->setupUi(this);
//    ui->tabWidget->tabBar()->setStyle(new CustomTabStyle);
}

FileManagerForm::~FileManagerForm()
{
    delete ui;
}

// [slot函数]退出按键
void FileManagerForm::on_pushButton_Esc_clicked()
{
    FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_filemanagerform_);
    delete FormFrame::GetInstance()->p_filemanagerform_;
    FormFrame::GetInstance()->p_filemanagerform_ = nullptr;
    FormFrame::GetInstance()->formstacked_id_.filemanagerform_id = -1;
    FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.mainform_id);
}

// [slot函数]控件槽函数



