#ifndef MACHINELEARNFORM_H
#define MACHINELEARNFORM_H
#include <QWidget>
#include <QDebug>
#include <QPair>
#include <QDate>

namespace Ui
{
    class MachineLearnWidget;
}
class QJsonAnalysis;

class MachineLearnForm : public QWidget
{
    Q_OBJECT
public:
    explicit MachineLearnForm(QWidget* parent = nullptr, bool isedit = false, QJsonAnalysis* pbuttoninfo = nullptr);
    ~MachineLearnForm();
private slots:
    void on_pushButton_Save_clicked();
    void on_pushButton_Revise_clicked();
    void on_pushButton_Esc_clicked();
private:
    Ui::MachineLearnWidget* ui;
    bool isEdit_;
    QJsonAnalysis* p_buttonInfo;
};

#endif
