#include "ButtonSelForm.h"
#include "ui_ButtonSelForm.h"
#include "Event/MainFormEvent.h"
#include "Form/MainForm.h"
#include "Mgr/FormFrame.h"
#include "Utils/ButtonInfo.h"
#include "Utils/SystemUtils.h"
#include <QDebug>

const qint32 PER_ROW_COUNT = 4;         // 每页显示的行数
const qint32 PER_COLUMN_COUNT = 3;      // 每页显示的列数

// [构造函数与析构函数]
ButtonSelForm::ButtonSelForm(QWidget* parent) : QWidget(parent), ui(new Ui::ButtonSelWidget)
{
    ui->setupUi(this);
    LoadAllConfigFileInfo();
    ShowButtonImage();
}

ButtonSelForm::~ButtonSelForm()
{
    delete ui;
    foreach (ButtonInfo* p_buttoninfo, v_pButtonOriginalInfo)
    {
        delete p_buttoninfo;
    }
}

// [成员函数]加载所有ini配置文件信息
void ButtonSelForm::LoadAllConfigFileInfo()
{
    // 列出配置文件目录下所有的ini纽扣配置文件
    //QString inifilepath_name(QCoreApplication::applicationDirPath() + QString("/Configs/"));
    QDir inifilepath(SystemUtils::GetPathForButtonConfigFile(), QString("*.ini"));
    QStringList inifilename_list = inifilepath.entryList(QDir::Files, QDir::Name);
    qint32 inifilecount = inifilename_list.size();
    //v_pButtonOriginalInfo.resize(inifilecount);

    // 生成全部纽扣信息widget指针vector
    p_currentButtonInfoVector = &v_pButtonOriginalInfo;
    p_currentButtonPageInfoStruct = &buttonOriginalPageInfo;
    for(qint32 i = 0; i < inifilecount; i++)
    {
        p_currentButtonInfoVector->push_back(new ButtonInfo(SystemUtils::GetPathForButtonConfigFile(), inifilename_list.at(i)));
        QObject::connect(p_currentButtonInfoVector->at(i), &ButtonInfo::SignalCmd_ButtonImageSelected, this, &ButtonSelForm::SetButtonSelectStaus);
    }
    // 初始化上一个以及当前被选取的纽扣图像指针
    p_lastSeletedButton = nullptr;
    p_currentSeletedButton = nullptr;
    // 初始化与翻页有关的变量
    InitButtonPageInfo();
}

// [成员函数]显示纽扣图片
void ButtonSelForm::ShowButtonImage()
{
    // 局部变量
    qint32 onepagecount = PER_ROW_COUNT * PER_COLUMN_COUNT;
    qint32 i = 0, j = 0;
    // 显示页码信息
    ui->labelPageAll->setText(QString("共%1页").arg(p_currentButtonPageInfoStruct->buttonImagePageCount));
    ui->labelPageCurrent->setText(QString("第%1页").arg(p_currentButtonPageInfoStruct->buttonImageCurrentPageNum));
    // 清除当前界面的纽扣图像
    while(ui->ButtonImageGridLayout->count() > 0)
    {
        QWidget* currentwidget = ui->ButtonImageGridLayout->itemAt(0)->widget();
        ui->ButtonImageGridLayout->removeWidget(currentwidget);
        // 如果currentwidget不是ButtonInfo就释放掉
        if(qobject_cast<ButtonInfo*>(currentwidget) == 0)
            delete currentwidget;
        else
            currentwidget->setParent(nullptr);
    }
    // 显示新的一页图像
    if(p_currentButtonPageInfoStruct->buttonImageCurrentPageNum < p_currentButtonPageInfoStruct->buttonImagePageCount)
    {
        j = 0;
        for(i = ((p_currentButtonPageInfoStruct->buttonImageCurrentPageNum - 1) * onepagecount);\
                  i < p_currentButtonPageInfoStruct->buttonImageCurrentPageNum * onepagecount; i++, j++)
        {
            ui->ButtonImageGridLayout->addWidget(p_currentButtonInfoVector->at(i), j / PER_COLUMN_COUNT, j % PER_COLUMN_COUNT, Qt::AlignCenter);
        }
    }
    else
    {
        j = 0;
        for(i = ((p_currentButtonPageInfoStruct->buttonImageCurrentPageNum - 1) * onepagecount);\
                  i < p_currentButtonInfoVector->size(); i++, j++)
        {
            ui->ButtonImageGridLayout->addWidget(p_currentButtonInfoVector->at(i), j / PER_COLUMN_COUNT, j % PER_COLUMN_COUNT, Qt::AlignCenter);
        }
        for(i = p_currentButtonPageInfoStruct->buttonImageLastPageCount; i < onepagecount; i++, j++)
        {
            ui->ButtonImageGridLayout->addWidget(new QWidget, j / PER_COLUMN_COUNT, j % PER_COLUMN_COUNT, Qt::AlignCenter);
        }
    }
}

// [成员函数]计算纽扣页面变量
void ButtonSelForm::InitButtonPageInfo()
{
    qint32 buttoninfowidget_count = p_currentButtonInfoVector->size();
    qint32 onepagecount = PER_ROW_COUNT * PER_COLUMN_COUNT;
    p_currentButtonPageInfoStruct->buttonImageCurrentPageNum = 1;
    if((buttoninfowidget_count % onepagecount) == 0)
    {
        p_currentButtonPageInfoStruct->buttonImagePageCount = buttoninfowidget_count / onepagecount;
        p_currentButtonPageInfoStruct->buttonImageLastPageCount = onepagecount;
    }
    else
    {
        p_currentButtonPageInfoStruct->buttonImagePageCount = buttoninfowidget_count / onepagecount + 1;
        p_currentButtonPageInfoStruct->buttonImageLastPageCount = buttoninfowidget_count % onepagecount;
    }
    // 上下翻页按键处理
    ui->pushButton_LastPage->setEnabled(false);
    if(p_currentButtonPageInfoStruct->buttonImagePageCount == 0)
        ui->pushButton_NextPage->setEnabled(false);
    else
        ui->pushButton_NextPage->setEnabled(true);
}

// [成员函数]根据条件刷新纽扣信息区信息
void ButtonSelForm::UpdateButtonInfoLabel(bool isshow)
{
    if(isshow)
    {
        ui->ButtonImage->setPixmap(*p_currentSeletedButton->GetButtonImagePtr()->pixmap());
        auto m = p_currentSeletedButton->GetButtonInfoPtr()->value("BaseInfoFront/shape").toString().toUtf8();
        ui->LabelInfo_XZ->setText(p_currentSeletedButton->GetButtonInfoPtr()->value("BaseInfoFront/shape").toString().toUtf8());
        ui->LabelInfo_XKS->setText(p_currentSeletedButton->GetButtonInfoPtr()->value("BaseInfoFront/hole").toString().toUtf8());
        ui->LabelInfo_YS->setText(p_currentSeletedButton->GetButtonInfoPtr()->value("BaseInfoFront/color").toString().toUtf8());
        ui->LabelInfo_ZF->setText(p_currentSeletedButton->GetButtonInfoPtr()->value("BaseInfoFront/char").toString().toUtf8());
        ui->LabelInfo_HW->setText(p_currentSeletedButton->GetButtonInfoPtr()->value("BaseInfoFront/pattern").toString().toUtf8());
        ui->LabelInfo_TMX->setText(p_currentSeletedButton->GetButtonInfoPtr()->value("BaseInfoFront/transmittance").toString().toUtf8());
        ui->LabelInfo_CZ->setText(p_currentSeletedButton->GetButtonInfoPtr()->value("BaseInfoFront/material").toString().toUtf8());
    }
    else
    {
        ui->LabelInfo_XZ->setText("无");
        ui->LabelInfo_XKS->setText("0");
        ui->LabelInfo_YS->setText("无");
        ui->LabelInfo_ZF->setText("无");
        ui->LabelInfo_HW->setText("无");
        ui->LabelInfo_TMX->setText("无");
        ui->LabelInfo_CZ->setText("无");
    }
}

// [slot函数]选中某个图片后触发
void ButtonSelForm::SetButtonSelectStaus(ButtonInfo* p_buttoninfo)
{
    // 添加选中边框
    p_currentSeletedButton = p_buttoninfo;
    if(p_lastSeletedButton != p_currentSeletedButton)
    {
        if(p_lastSeletedButton != nullptr)
            p_lastSeletedButton->GetButtonImagePtr()->setStyleSheet("");
        p_currentSeletedButton->GetButtonImagePtr()->setStyleSheet("border:4px solid #FF0000");
        // 记录选中的纽扣信息文件名称
        currentSeletedButtonName = p_currentSeletedButton->GetButtonNamePtr()->text();
        p_lastSeletedButton = p_currentSeletedButton;
    }
    // 更新信息区信息
    UpdateButtonInfoLabel(true);
}

// [控件slot函数]纽扣图像上一页按键
void ButtonSelForm::on_pushButton_LastPage_clicked()
{
    // 按键锁定判断
    if(p_currentButtonPageInfoStruct->buttonImageCurrentPageNum  == p_currentButtonPageInfoStruct->buttonImagePageCount)
        ui->pushButton_NextPage->setEnabled(true);
    if((p_currentButtonPageInfoStruct->buttonImageCurrentPageNum -= 1) == 1)
        ui->pushButton_LastPage->setEnabled(false);
    // 刷新图像显示
    ShowButtonImage();
}

// [控件slot函数]纽扣图像下一页按键
void ButtonSelForm::on_pushButton_NextPage_clicked()
{
    // 按键锁定判断
    if(p_currentButtonPageInfoStruct->buttonImageCurrentPageNum  == 1)
        ui->pushButton_LastPage->setEnabled(true);
    if((p_currentButtonPageInfoStruct->buttonImageCurrentPageNum += 1) == p_currentButtonPageInfoStruct->buttonImagePageCount)
        ui->pushButton_NextPage->setEnabled(false);
    // 刷新图像显示
    ShowButtonImage();
}

// [控件slot函数]纽扣取消选择按键
void ButtonSelForm::on_pushButton_Esc_clicked()
{
    // 清除当前Form
    FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_buttonselform_);
    delete FormFrame::GetInstance()->p_buttonselform_;
    FormFrame::GetInstance()->p_buttonselform_ = nullptr;
    FormFrame::GetInstance()->formstacked_id_.buttonselform_id = -1;
    FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.mainform_id);
}

// [控件slot函数]纽扣确定选择按键
void ButtonSelForm::on_pushButton_Enter_clicked()
{
    // 向MainForm投递一个事件（被选纽扣的ID编号）
    MainFormEvent* mainformevent = new MainFormEvent(MainFormEvent::EventType_ButtonSelectedResult);
    mainformevent->button_id_ = currentSeletedButtonName;
    // 注意一定要加入mainform的头文件，否则post出错
    QCoreApplication::postEvent(FormFrame::GetInstance()->p_mainform_, mainformevent);
    // 清除当前Form
    FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_buttonselform_);
    delete FormFrame::GetInstance()->p_buttonselform_;
    FormFrame::GetInstance()->p_buttonselform_ = nullptr;
    FormFrame::GetInstance()->formstacked_id_.buttonselform_id = -1;
    FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.mainform_id);
}

// [控件slot函数]纽扣检索按键
void ButtonSelForm::on_pushButton_Find_clicked()
{
    // 指针指向筛选纽扣图像缓存区
    p_currentButtonInfoVector = &v_pButtonSelectedInfo;
    p_currentButtonInfoVector->resize(0);
    p_currentButtonPageInfoStruct = &buttonSelectedPageInfo;
    // 首先根据纽扣ID号查询
    QString button_id = ui->lineEdit_ButtonID->text();
    foreach (ButtonInfo* p_buttoninfo, v_pButtonOriginalInfo)
    {
        if(p_buttoninfo->GetButtonInfoPtr()->value("BaseInfoFront/id").toString().contains(button_id, Qt::CaseInsensitive))
        {
            p_currentButtonInfoVector->push_back(p_buttoninfo);
        }
    }
    // 显示查询结果
    InitButtonPageInfo();
    ShowButtonImage();
}

// [控件slot函数]纽扣清空检索条件按键
void ButtonSelForm::on_pushButton_Clear_clicked()
{
    // 指针指向原始纽扣图像缓存区
    p_currentButtonInfoVector = &v_pButtonOriginalInfo;
    p_currentButtonPageInfoStruct = &buttonOriginalPageInfo;
    p_currentButtonPageInfoStruct->buttonImageCurrentPageNum = 1;
    // 复位检索条件
    ui->lineEdit_ButtonID->setText("");
    // 复位已经选中的图片
    if(p_currentSeletedButton)
    {
        p_currentSeletedButton->GetButtonImagePtr()->setStyleSheet("");
        UpdateButtonInfoLabel(false);
        currentSeletedButtonName.clear();
        p_currentSeletedButton = nullptr;
    }
    // 刷新图像显示
    ShowButtonImage();
}


