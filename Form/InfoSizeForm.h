#ifndef INFOSIZEFORM_H
#define INFOSIZEFORM_H
#include <QWidget>
#include <QMouseEvent>
#include "Utils/QJsonAnalysis.h"

namespace Ui
{
    class InfoSizeWidget;
}

class InfoSizeForm : public QWidget
{
    Q_OBJECT
public:
    explicit InfoSizeForm(QWidget* parent = nullptr);
    ~InfoSizeForm();
    Ui::InfoSizeWidget* GetWidgetPtr() { return ui; }
    void Show(QJsonAnalysis* p_buttonjson);
    void Update(qint32 buf[]);

signals:

public slots:

private slots:
    void on_pushButton_clicked();

protected:
    virtual void mousePressEvent(QMouseEvent *event);
    virtual void mouseMoveEvent(QMouseEvent *event);
    virtual void mouseReleaseEvent(QMouseEvent *event);

private:
    Ui::InfoSizeWidget* ui;
    bool ismoveing;
    QPoint moveposition;
};

#endif
