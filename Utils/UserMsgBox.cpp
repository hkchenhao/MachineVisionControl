#include "UserMsgBox.h"

/******************************普通信息框***************************************/
const QFont CTextFont("Yahei Mono", 16, 16, false);
const qint32 CTimeOut = 1500;

UserTextMsgBox::UserTextMsgBox(const QString& instr, qint32 hpos, QWidget* parent) : QWidget(parent)
{
    this->setStyleSheet("QWidget{background-color:gray;border-radius:15px;}");
    p_textLabel = new QLabel(instr, this);
    p_textLabel->setFont(CTextFont);
    p_textLabel->setGeometry(0, 0, (instr.length() + 8) * CTextFont.pointSize(), CTextFont.weight() * 3);
    p_textLabel->setAlignment(Qt::AlignCenter);
    this->move((parent->width() - p_textLabel->width()) / 2, parent->height() - hpos);
    // 定时器超时后删除MsgBox
    QTimer::singleShot(CTimeOut, this, [this](){ this->deleteLater(); });
}

UserTextMsgBox::UserTextMsgBox(const QString& instr, qint32 len, qint32 hpos, QWidget* parent) : QWidget(parent)
{
    this->setStyleSheet("QWidget{background-color:gray;border-radius:15px;}");
    p_textLabel = new QLabel(instr, this);
    p_textLabel->setFont(CTextFont);
    p_textLabel->setGeometry(0, 0, len, CTextFont.weight() * 3);
    p_textLabel->setAlignment(Qt::AlignCenter);
    this->move((parent->width() - p_textLabel->width()) / 2, parent->height() - hpos);
    // 定时器超时后删除MsgBox
    QTimer::singleShot(CTimeOut, this, [this](){ this->deleteLater(); });
}


/******************************等待信息框***************************************/
const QFont CLoadingTextFont("Yahei Mono", 16, 16, false);
const QSize CLoadingMsgBoxSize(500, CLoadingTextFont.weight() * 3);

UserLoadingMsgBox::UserLoadingMsgBox(const QString& instr, qint32 hpos, QWidget* parent)
{
    this->setStyleSheet("QDialog{background-color:gray;border-radius:15px;}");
    this->setModal(true);           // 模态对话框
    this->setWindowFlags(Qt::Dialog | Qt::FramelessWindowHint);
    this->setAttribute(Qt::WA_DeleteOnClose);
    this->setWindowOpacity(1.0);
    this->setFixedSize(CLoadingMsgBoxSize);
    this->move((parent->width() - width()) / 2, parent->height() - hpos);
    // 加载字体与动画
    QLabel* ploadinglabel = new QLabel(this);
    QLabel* ptextlabel = new QLabel(instr, this);
    QMovie* ploadgif = new QMovie(":/icon/loading-32px.gif", QByteArray(), this);
    ploadinglabel->setGeometry(0, 0, this->height(), this->height());
    ploadinglabel->setAlignment(Qt::AlignCenter);
    ploadinglabel->setMovie(ploadgif);
    ptextlabel->setFont(CLoadingTextFont);
    ptextlabel->setGeometry(this->height(), 0, this->width() - this->height(), this->height());
    ptextlabel->setAlignment(Qt::AlignCenter);
    ploadgif->start();
//    QTimer::singleShot(3000, this, [this]() { this->accept(); });
}
