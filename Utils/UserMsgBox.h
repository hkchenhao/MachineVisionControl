#ifndef USERMSGBOX_H
#define USERMSGBOX_H
#include <QWidget>
#include <QDialog>
#include <QLabel>
#include <QMovie>
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

class UserLoadingMsgBox : public QDialog
{
    Q_OBJECT
public:
    explicit UserLoadingMsgBox(const QString& instr, qint32 hpos, QWidget* parent = Q_NULLPTR);
};


#endif
