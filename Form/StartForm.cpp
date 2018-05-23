#include "StartForm.h"
#include "ui_StartForm.h"
#include "Form/MainForm.h"
#include "Mgr/FormFrame.h"
#include <QDebug>

// [构造函数与析构函数]
StartForm::StartForm(QWidget* parent) : QWidget(parent), ui(new Ui::StartWidget)
{
    ui->setupUi(this);
    ui->label_Prompt->setText("");
    //this->setAutoFillBackground(true);
//    QPixmap bgImages(":/pic/background.jpg");
//    QPalette bgPalette = this->palette();
//    bgPalette.setBrush(QPalette::Background,bgImages);
//    this->setPalette(bgPalette);

//    ui->comboBox_UserName->setView(new QListView(ui->comboBox_UserName));
//    ui->comboBox_UserName->addItem("qaz");
//    ui->comboBox_UserName->addItem("zaq");
//    auto m = ui->comboBox_UserName->lineEdit();
//    ui->comboBox_UserName->lineEdit()->setReadOnly(true); //设为不可编辑
//    QFont fontle;
//    fontle.setFamily(tr("宋体"));
//    fontle.setPixelSize(12); // 设了字体
//    ui->comboBox_UserName->lineEdit()->setAlignment(Qt::AlignCenter);  // 这是QComboBox里面的文字居中
//    for (int i = 0; i < ui->comboBox_UserName->count(); ++i)
//    {  // 这是QComboBox下拉框的文字居中
//        ui->comboBox_UserName->setItemData(i, fontle, Qt::FontRole); //我还加了字体，同理，只对下拉框有效
//        ui->comboBox_UserName->setItemData(i, Qt::AlignCenter, Qt::TextAlignmentRole); //居中
//    }
}

StartForm::~StartForm()
{
    delete ui;
}

// [控件slot函数]comboBox用户改变
void StartForm::on_comboBox_UserName_currentIndexChanged(int index)
{
    // 根据不同用户设置提示信息
    switch (index)
    {
        case 0:
            ui->lineEdit_Passwd->setPlaceholderText("无需密码，请直接登录");
            ui->pushButton_Load->setEnabled(true);
            break;
        case 1:
            ui->lineEdit_Passwd->setPlaceholderText("请输入6位密码");
            ui->pushButton_Load->setEnabled(false);
            break;
        case 2:
            ui->lineEdit_Passwd->setPlaceholderText("请输入6位密码");
            ui->pushButton_Load->setEnabled(false);
            break;
        default:
            break;
    }
}

// [控件slot函数]密码输入改变
void StartForm::on_lineEdit_Passwd_textEdited(const QString& arg1)
{
    if(ui->comboBox_UserName->currentIndex() != 0)
    {
        if(arg1 == "")
            ui->pushButton_Load->setEnabled(false);
        else
            ui->pushButton_Load->setEnabled(true);
        // 清除密码错误提示信息
        ui->label_Prompt->setText("");
    }
}

// [控件slot函数]登录按键
void StartForm::on_pushButton_Load_clicked()
{
    // 根据不同用户判断登录
    switch (ui->comboBox_UserName->currentIndex())
    {
        // USER1
        case 0:
            FormFrame::GetInstance()->p_mainform_->SetUserAccessPermission(0);
            break;
        // USER1
        case 1:
            if(ui->lineEdit_Passwd->text() != "123456")
            {
                ui->label_Prompt->setText("密码输入错误 请重新输入！");
                return;
            }
            FormFrame::GetInstance()->p_mainform_->SetUserAccessPermission(1);
            break;
        // USER1
        case 2:
            if(ui->lineEdit_Passwd->text() != "123456")
            {
                ui->label_Prompt->setText("密码输入错误 请重新输入！");
                return;
            }
            break;
        default:
            return;
            break;
    }
    // 加载主界面
    FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.mainform_id);
    // 清除当前Form
    FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_startform_);
    delete FormFrame::GetInstance()->p_startform_;
    FormFrame::GetInstance()->p_startform_ = nullptr;
    FormFrame::GetInstance()->formstacked_id_.startform_id = -1;
}




