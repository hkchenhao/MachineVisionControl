#ifndef USERMSGBOX_H
#define USERMSGBOX_H
#include <QWidget>
#include <QLabel>
#include <QTimer>
#include <QString>

class UserTextMsgBox : public QWidget
{
    Q_OBJECT
public:
    explicit UserTextMsgBox(const QString& instr, qint32 hpos, QWidget* parent = nullptr);
    explicit UserTextMsgBox(const QString& instr, qint32 len, qint32 hpos, QWidget* parent = nullptr);
private:
    QLabel* p_textLabel;
    QTimer* p_msgboxTimer;
};

#endif
