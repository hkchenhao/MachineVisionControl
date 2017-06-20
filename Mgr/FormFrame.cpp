#include "FormFrame.h"
#include "Form/StartForm.h"
#include "Form/MainForm.h"
#include "Form/ButtonSelForm.h"
#include "Form/StatisticalForm.h"

FormFrame::FormFrame(QWidget* parent) : QWidget(parent)
{
    // 全局指针初始化
    formstacked_id_.startform_id = -1;
    formstacked_id_.mainform_id = -1;
    formstacked_id_.buttonselform_id = -1;
    formstacked_id_.statisticalform_id = -1;
    p_formstacked_ = nullptr;
    // Form指针
    p_startform_ = nullptr;
    p_mainform_ = nullptr;
    p_buttonselform_ = nullptr;
    p_statisticalform_ = nullptr;
    // Widget布局
    QVBoxLayout* p_layout = new QVBoxLayout;
    p_layout->setContentsMargins(0, 0, 0, 0);
    p_layout->setSpacing(0);
    p_formstacked_ = new QStackedWidget;
    p_layout->addWidget(p_formstacked_);
    // Widget属性定义
    this->setFixedSize(1024, 768);
#ifdef __arm__
    this->setWindowFlags(Qt::FramelessWindowHint);
    this->setAttribute(Qt::WA_DeleteOnClose);
#endif
    this->setLayout(p_layout);
    // 通过p_formstacked_管理界面
    formstacked_id_.startform_id = p_formstacked_->addWidget(p_startform_ = new StartForm);
    formstacked_id_.mainform_id = p_formstacked_->addWidget(p_mainform_ = new MainForm);
    //formstacked_id_.statisticalform_id = p_formstacked_->addWidget(p_statisticalform_ = new StatisticalForm);
    p_formstacked_->setCurrentIndex(formstacked_id_.mainform_id);
}

FormFrame::~FormFrame()
{

}


