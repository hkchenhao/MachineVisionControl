#include "UserMsgBox.h"

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



