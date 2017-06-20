#ifndef LOADDIALOG_H
#define LOADDIALOG_H
#include <QDialog>

// 前置声明减少头文件的包含
class QLabel;
class QMovie;

class LoadDialog : public QDialog
{
    Q_OBJECT
public:
    LoadDialog(QWidget* parent = Q_NULLPTR, Qt::WindowFlags f = Qt::WindowFlags());

protected:
    void customEvent(QEvent* event);

private:
    QLabel* p_loadingText;
    QLabel* p_loadingLabel;
    QMovie* p_loadingMovie;
};

#endif
