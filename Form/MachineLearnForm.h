#ifndef MACHINELEARNFORM_H
#define MACHINELEARNFORM_H
#include <QWidget>
#include <QMessageBox>
#include <QDate>
#include <QDebug>
#include <QVector>
#include <QString>
#include <QList>
#include <QMap>

namespace Ui
{
    class MachineLearnWidget;
}
class QJsonAnalysis;

class MachineLearnForm : public QWidget
{
    Q_OBJECT
public:
    explicit MachineLearnForm(QWidget* parent = nullptr, bool isedit = false,
                              QString* pjsonpath = nullptr, QJsonAnalysis* pbuttonjsoninfo = nullptr, QString* jsonname = nullptr);
    ~MachineLearnForm();
signals:
    void SignalUpdateButtonSelectForm();
private slots:
    void on_pushButton_Save_clicked();
    void on_pushButton_Revise_clicked();
    void on_pushButton_Esc_clicked();
    void on_box_cz_0_currentIndexChanged(int index);
    void on_box_xz_0_currentIndexChanged(int index);
    void on_box_xks_0_currentIndexChanged(int index);
    void on_box_tmx_0_currentIndexChanged(int index);

private:
    void InitFormWidget();
    void SaveButtonJsonInfo(QJsonAnalysis* pjsoninfo);
    Ui::MachineLearnWidget* ui;
    bool isEdit_;
    QString buttonJsonName;
    QString* p_buttonJsonPath;
    QJsonAnalysis* p_buttonJsonInfo;
};

#endif
