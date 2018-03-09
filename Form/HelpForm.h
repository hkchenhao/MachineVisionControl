#ifndef HELPFORM_H
#define HELPFORM_H
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
    class HelpWidget;
}

class HelpForm : public QWidget
{
    Q_OBJECT
public:
    explicit HelpForm(QWidget* parent = nullptr);
    ~HelpForm();
signals:

private slots:

    void on_pushButton_Esc_clicked();

private:
    Ui::HelpWidget* ui;
};

#endif
